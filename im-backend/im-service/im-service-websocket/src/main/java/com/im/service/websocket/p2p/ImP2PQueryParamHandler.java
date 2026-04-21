package com.im.service.websocket.p2p;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 在 WebSocket 握手前提取 URL Query 参数存到 Channel attr
 */
@Slf4j
public class ImP2PQueryParamHandler extends ChannelInboundHandlerAdapter {

    public static final AttributeKey<String> ATTR_TOKEN = AttributeKey.valueOf("token");
    public static final AttributeKey<String> ATTR_USER_ID = AttributeKey.valueOf("userId");
    public static final AttributeKey<String> ATTR_DEVICE_ID = AttributeKey.valueOf("deviceId");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.uri();
            int q = uri.indexOf('?');
            if (q >= 0) {
                String query = uri.substring(q + 1);
                String token = extractParam(query, "token");
                String userId = extractParam(query, "userId");
                String deviceId = extractParam(query, "deviceId");

                if (token != null) ctx.channel().attr(ATTR_TOKEN).set(token);
                if (userId != null) ctx.channel().attr(ATTR_USER_ID).set(userId);
                if (deviceId != null) ctx.channel().attr(ATTR_DEVICE_ID).set(deviceId);

                log.debug("提取 P2P Query 参数: userId={}, deviceId={}", userId, deviceId);
            }
        }
        super.channelRead(ctx, msg);
    }

    private String extractParam(String query, String name) {
        if (query == null || name == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(name)) {
                return kv[1];
            }
        }
        return null;
    }
}
