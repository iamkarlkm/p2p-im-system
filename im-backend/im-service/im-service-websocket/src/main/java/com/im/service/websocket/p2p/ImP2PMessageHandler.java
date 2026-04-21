package com.im.service.websocket.p2p;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import p2pws.P2PControl;
import p2pws.P2PWrapperOuterClass;
import p2pws.sdk.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * IM P2P WebSocket 消息处理器 (简化版，移除 Spring WebSocket 依赖)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImP2PMessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    public static final AttributeKey<String> ATTR_USER_ID = AttributeKey.valueOf("userId");
    public static final AttributeKey<String> ATTR_DEVICE_ID = AttributeKey.valueOf("deviceId");
    public static final AttributeKey<Boolean> ATTR_ENCRYPTED = AttributeKey.valueOf("encrypted");
    public static final AttributeKey<Long> ATTR_OFFSET = AttributeKey.valueOf("offset");

    private final ObjectMapper objectMapper;
    private final P2PChannelSessionManager sessionManager;

    private KeyFileProvider keyFileProvider;
    private byte[] keyId32;
    private long keyLen;
    private int magic = 0x1234;
    private int version = 1;
    private int flagsPlain = 4;
    private int flagsEncrypted = 5;
    private int maxFramePayload = 4 * 1024 * 1024;
    private String rsaPrivateKeyPem;
    private final SecureRandom rnd = new SecureRandom();

    public void setKeyFileProvider(KeyFileProvider provider, byte[] keyId32, long keyLen) {
        this.keyFileProvider = provider;
        this.keyId32 = Arrays.copyOf(keyId32, keyId32.length);
        this.keyLen = keyLen;
    }

    public void setCryptoParams(int magic, int version, int flagsPlain, int flagsEncrypted, int maxFramePayload) {
        this.magic = magic;
        this.version = version;
        this.flagsPlain = flagsPlain;
        this.flagsEncrypted = flagsEncrypted;
        this.maxFramePayload = maxFramePayload;
    }

    public void setRsaPrivateKeyPem(String pem) {
        this.rsaPrivateKeyPem = pem;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        ByteBuf content = frame.content();
        byte[] ws = new byte[content.readableBytes()];
        content.readBytes(ws);

        WireFrame wf = FrameCodec.decode(ws);
        byte[] payload = wf.cipherPayload();
        boolean encrypted = Boolean.TRUE.equals(ctx.channel().attr(ATTR_ENCRYPTED).get());
        Long offset = ctx.channel().attr(ATTR_OFFSET).get();

        byte[] plain = encrypted ? XorCipher.xorWithKeyFile(payload, keyFileProvider, keyId32, offset) : payload;
        P2PWrapperOuterClass.P2PWrapper wrapper = P2PWrapperCodec.decode(plain);

        int cmd = wrapper.getCommand();
        if (cmd == -10001) {
            handleHand(ctx, wrapper);
            return;
        }

        if (!encrypted) {
            sendError(ctx, "HANDSHAKE_REQUIRED", "请先完成握手");
            return;
        }

        if (cmd >= 20001 && cmd <= 20012) {
            handleImMessage(ctx, wrapper);
        } else {
            log.warn("未知命令: cmd={}", cmd);
            sendError(ctx, "UNKNOWN_COMMAND", "Unknown command: " + cmd);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("P2P 通道激活: {}", ctx.channel().id().asLongText());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = parseLong(ctx.channel().attr(ATTR_USER_ID).get());
        String deviceId = ctx.channel().attr(ATTR_DEVICE_ID).get();
        if (userId != null && deviceId != null) {
            sessionManager.unregisterSession(userId, deviceId);
        } else {
            sessionManager.unregisterByChannel(ctx.channel());
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("P2P 通道异常: {}", ctx.channel().id().asLongText(), cause);
        ctx.close();
    }

    private void handleHand(ChannelHandlerContext ctx, P2PWrapperOuterClass.P2PWrapper wrapper) {
        try {
            P2PControl.Hand hand = P2PControl.Hand.parseFrom(wrapper.getData());
            boolean ok = false;
            for (ByteString kid : hand.getKeyIdsList()) {
                byte[] b = kid.toByteArray();
                if (b.length == 32 && Arrays.equals(b, keyId32)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                ctx.close();
                return;
            }

            int maxPayload = maxFramePayload;
            if (hand.getMaxFramePayload() > 0 && hand.getMaxFramePayload() < maxPayload) {
                maxPayload = hand.getMaxFramePayload();
            }
            if (keyLen <= maxPayload) {
                ctx.close();
                return;
            }
            long maxOffset = keyLen - maxPayload;
            long off = (Integer.toUnsignedLong(rnd.nextInt()) % maxOffset);

            byte[] sessionId = new byte[16];
            rnd.nextBytes(sessionId);
            P2PControl.HandAckPlain ackPlain = P2PControl.HandAckPlain.newBuilder()
                    .setSessionId(ByteString.copyFrom(sessionId))
                    .setSelectedKeyId(ByteString.copyFrom(keyId32))
                    .setOffset((int) off)
                    .setMaxFramePayload(maxPayload)
                    .setHeaderPolicyId(0)
                    .build();

            PublicKey clientPub = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(hand.getClientPubkey().toByteArray()));
            byte[] encryptedAck = RsaOaep.encryptSha256(clientPub, ackPlain.toByteArray());

            P2PWrapperOuterClass.P2PWrapper resp = P2PWrapperOuterClass.P2PWrapper.newBuilder()
                    .setSeq(wrapper.getSeq())
                    .setCommand(-10002)
                    .setData(ByteString.copyFrom(encryptedAck))
                    .build();

            writePlain(ctx, resp);
            ctx.channel().attr(ATTR_ENCRYPTED).set(true);
            ctx.channel().attr(ATTR_OFFSET).set(off);

            String userIdStr = ctx.channel().attr(ImP2PQueryParamHandler.ATTR_USER_ID).get();
            String deviceId = ctx.channel().attr(ImP2PQueryParamHandler.ATTR_DEVICE_ID).get();
            if (deviceId == null || deviceId.isEmpty()) {
                deviceId = "device_" + System.currentTimeMillis();
            }

            Long userId = parseLong(userIdStr);
            if (userId != null) {
                ctx.channel().attr(ATTR_USER_ID).set(userIdStr);
                ctx.channel().attr(ATTR_DEVICE_ID).set(deviceId);
                sessionManager.registerSession(userId, deviceId, ctx.channel());
            }

            log.info("P2P 握手成功: channelId={}, userId={}, deviceId={}",
                    ctx.channel().id().asLongText(), userId, deviceId);

        } catch (Exception e) {
            log.error("P2P 握手失败", e);
            ctx.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleImMessage(ChannelHandlerContext ctx, P2PWrapperOuterClass.P2PWrapper wrapper) {
        try {
            String json = new String(wrapper.getData().toByteArray(), StandardCharsets.UTF_8);
            Map<String, Object> msg = objectMapper.readValue(json, Map.class);
            sessionManager.updateLastActivityTime(ctx.channel());

            Long userId = parseLong(ctx.channel().attr(ATTR_USER_ID).get());
            if (userId != null && !msg.containsKey("senderId")) {
                msg.put("senderId", userId);
            }

            String type = (String) msg.get("type");
            if (type == null) {
                sendError(ctx, "MISSING_TYPE", "Message type is required");
                return;
            }

            switch (type) {
                case "ping":
                    handlePing(ctx, wrapper.getSeq());
                    break;
                case "chat":
                    handleChat(ctx, wrapper.getSeq(), msg);
                    break;
                case "group_chat":
                    handleGroupChat(ctx, wrapper.getSeq(), msg);
                    break;
                case "read_receipt":
                    handleReadReceipt(ctx, wrapper.getSeq(), msg);
                    break;
                case "typing":
                    handleTyping(ctx, wrapper.getSeq(), msg);
                    break;
                case "presence":
                    handlePresence(ctx, msg);
                    break;
                default:
                    sendError(ctx, "UNKNOWN_MESSAGE_TYPE", "Unknown type: " + type);
            }
        } catch (Exception e) {
            log.error("处理 IM 消息失败", e);
            sendError(ctx, "MESSAGE_PARSE_ERROR", e.getMessage());
        }
    }

    private void handlePing(ChannelHandlerContext ctx, int seq) {
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "pong");
        pong.put("timestamp", System.currentTimeMillis());
        pong.put("payload", Map.of("serverTime", System.currentTimeMillis()));
        sendImMessage(ctx, seq, 20007, pong);
    }

    @SuppressWarnings("unchecked")
    private void handleChat(ChannelHandlerContext ctx, int seq, Map<String, Object> msg) {
        Number receiverIdNum = (Number) msg.get("receiverId");
        if (receiverIdNum == null) {
            sendError(ctx, "MISSING_RECEIVER", "Receiver ID is required");
            return;
        }
        Long receiverId = receiverIdNum.longValue();
        msg.put("timestamp", System.currentTimeMillis());

        String messageId = (String) msg.get("messageId");
        sendAck(ctx, seq, messageId);

        Set<Channel> channels = sessionManager.getUserChannels(receiverId);
        boolean delivered = false;
        for (Channel ch : channels) {
            if (ch != null && ch.isActive()) {
                sendImMessage(ch, seq, 20001, msg);
                delivered = true;
            }
        }

        if (delivered) {
            Map<String, Object> receipt = new HashMap<>();
            receipt.put("type", "delivery_receipt");
            receipt.put("messageId", messageId);
            receipt.put("timestamp", System.currentTimeMillis());
            receipt.put("payload", Map.of("receiverId", receiverId, "deliveryTime", System.currentTimeMillis()));
            sendImMessage(ctx, 0, 20008, receipt);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleGroupChat(ChannelHandlerContext ctx, int seq, Map<String, Object> msg) {
        Number groupIdNum = (Number) msg.get("groupId");
        if (groupIdNum == null) {
            sendError(ctx, "MISSING_GROUP_ID", "Group ID is required");
            return;
        }
        msg.put("timestamp", System.currentTimeMillis());
        String messageId = (String) msg.get("messageId");
        sendAck(ctx, seq, messageId);

        List<Number> memberIds = (List<Number>) ((Map<String, Object>) msg.getOrDefault("payload", Map.of())).get("memberIds");
        Number senderIdNum = (Number) msg.get("senderId");
        Long senderId = senderIdNum != null ? senderIdNum.longValue() : null;

        if (memberIds != null) {
            for (Number memberIdNum : memberIds) {
                Long memberId = memberIdNum.longValue();
                if (!memberId.equals(senderId)) {
                    Set<Channel> channels = sessionManager.getUserChannels(memberId);
                    for (Channel ch : channels) {
                        if (ch != null && ch.isActive()) {
                            sendImMessage(ch, seq, 20002, msg);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleReadReceipt(ChannelHandlerContext ctx, int seq, Map<String, Object> msg) {
        String messageId = (String) msg.get("messageId");
        Number senderIdNum = (Number) msg.get("senderId");
        if (messageId == null || senderIdNum == null) return;

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("type", "read_receipt");
        receipt.put("messageId", messageId);
        receipt.put("senderId", senderIdNum.longValue());
        receipt.put("timestamp", System.currentTimeMillis());
        receipt.put("payload", Map.of("messageId", messageId, "readerId", senderIdNum.longValue(), "readTime", System.currentTimeMillis()));

        Number targetIdNum = (Number) msg.get("receiverId");
        if (targetIdNum != null) {
            Set<Channel> channels = sessionManager.getUserChannels(targetIdNum.longValue());
            for (Channel ch : channels) {
                if (ch != null && ch.isActive()) {
                    sendImMessage(ch, seq, 20003, receipt);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleTyping(ChannelHandlerContext ctx, int seq, Map<String, Object> msg) {
        Number receiverIdNum = (Number) msg.get("receiverId");
        Number groupIdNum = (Number) msg.get("groupId");
        msg.put("timestamp", System.currentTimeMillis());

        if (groupIdNum != null) {
            List<Number> memberIds = (List<Number>) ((Map<String, Object>) msg.getOrDefault("payload", Map.of())).get("memberIds");
            Number senderIdNum = (Number) msg.get("senderId");
            Long senderId = senderIdNum != null ? senderIdNum.longValue() : null;
            if (memberIds != null) {
                for (Number memberIdNum : memberIds) {
                    Long memberId = memberIdNum.longValue();
                    if (!memberId.equals(senderId)) {
                        Set<Channel> channels = sessionManager.getUserChannels(memberId);
                        for (Channel ch : channels) {
                            if (ch != null && ch.isActive()) sendImMessage(ch, seq, 20005, msg);
                        }
                    }
                }
            }
        } else if (receiverIdNum != null) {
            Set<Channel> channels = sessionManager.getUserChannels(receiverIdNum.longValue());
            for (Channel ch : channels) {
                if (ch != null && ch.isActive()) sendImMessage(ch, seq, 20005, msg);
            }
        }
    }

    private void handlePresence(ChannelHandlerContext ctx, Map<String, Object> msg) {
        String status = (String) msg.get("status");
        Long userId = parseLong(ctx.channel().attr(ATTR_USER_ID).get());
        log.debug("用户 {} 更新在线状态: {}", userId, status);
    }

    private void sendAck(ChannelHandlerContext ctx, int seq, String messageId) {
        Map<String, Object> ack = new HashMap<>();
        ack.put("type", "ack");
        ack.put("messageId", messageId);
        ack.put("timestamp", System.currentTimeMillis());
        ack.put("payload", Map.of("serverTime", System.currentTimeMillis()));
        sendImMessage(ctx, seq, 20007, ack);
    }

    private void sendError(ChannelHandlerContext ctx, String code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "error");
        error.put("timestamp", System.currentTimeMillis());
        error.put("payload", Map.of("code", code, "message", message));
        sendImMessage(ctx, 0, 20012, error);
    }

    private void sendImMessage(ChannelHandlerContext ctx, int seq, int command, Map<String, Object> msg) {
        sendImMessage(ctx.channel(), seq, command, msg);
    }

    private void sendImMessage(Channel channel, int seq, int command, Map<String, Object> msg) {
        try {
            String json = objectMapper.writeValueAsString(msg);
            byte[] plain = json.getBytes(StandardCharsets.UTF_8);
            P2PWrapperOuterClass.P2PWrapper wrapper = P2PWrapperOuterClass.P2PWrapper.newBuilder()
                    .setSeq(seq)
                    .setCommand(command)
                    .setData(ByteString.copyFrom(plain))
                    .build();

            Long offset = channel.attr(ATTR_OFFSET).get();
            Boolean encrypted = channel.attr(ATTR_ENCRYPTED).get();
            byte[] payload = (Boolean.TRUE.equals(encrypted) && offset != null)
                    ? XorCipher.xorWithKeyFile(plain, keyFileProvider, keyId32, offset)
                    : plain;

            WireHeader header = new WireHeader(payload.length, magic,
                    Boolean.TRUE.equals(encrypted) ? flagsEncrypted : flagsPlain, version);
            byte[] frame = FrameCodec.encode(header, payload);

            channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(frame)));
        } catch (Exception e) {
            log.error("发送 P2P 消息失败", e);
        }
    }

    private void writePlain(ChannelHandlerContext ctx, P2PWrapperOuterClass.P2PWrapper wrapper) {
        byte[] payload = P2PWrapperCodec.encode(wrapper);
        WireHeader header = new WireHeader(payload.length, magic, flagsPlain, version);
        byte[] frame = FrameCodec.encode(header, payload);
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(frame)));
    }

    private Long parseLong(String value) {
        if (value == null) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
