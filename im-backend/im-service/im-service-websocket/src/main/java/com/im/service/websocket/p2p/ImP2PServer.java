package com.im.service.websocket.p2p;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import p2pws.sdk.FileKeyFileProvider;
import p2pws.sdk.InMemoryKeyFileProvider;
import p2pws.sdk.KeyFileProvider;
import p2pws.sdk.KeyId;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Base64;

/**
 * IM P2P WebSocket 服务器
 * 基于 Netty + p2p-ws-sdk-java
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImP2PServer {

    private final ImP2PMessageHandler imP2PMessageHandler;

    @Value("${im.p2p.port:9000}")
    private int port;

    @Value("${im.p2p.path:/p2p}")
    private String path;

    @Value("${im.p2p.magic:4660}")
    private int magic; // 0x1234

    @Value("${im.p2p.version:1}")
    private int version;

    @Value("${im.p2p.maxFramePayload:4194304}")
    private int maxFramePayload;

    @Value("classpath:p2p/keyfile.im.key")
    private Resource keyfileResource;

    @Value("classpath:p2p/im_server.pem")
    private Resource rsaPrivateKeyResource;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @PostConstruct
    public void start() throws Exception {
        KeyFileProvider provider;
        byte[] keyId32;
        long keyLen;
        String rsaPem;

        if (keyfileResource != null && keyfileResource.exists()) {
            Path keyfileTemp = Files.createTempFile("p2p_keyfile_", ".key");
            try (var in = keyfileResource.getInputStream()) {
                Files.copy(in, keyfileTemp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            FileKeyFileProvider fileProvider = new FileKeyFileProvider();
            fileProvider.put(keyfileTemp);
            provider = fileProvider;
            keyId32 = FileKeyFileProvider.sha256(keyfileTemp);
            keyLen = Files.size(keyfileTemp);
            log.info("使用外部 keyfile: {}", keyfileTemp);
        } else {
            // 自动生成 5MB 随机 in-memory keyfile
            byte[] keyfileBytes = new byte[5 * 1024 * 1024];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(keyfileBytes);
            InMemoryKeyFileProvider memProvider = new InMemoryKeyFileProvider();
            keyId32 = java.security.MessageDigest.getInstance("SHA-256").digest(keyfileBytes);
            memProvider.put(keyId32, keyfileBytes);
            provider = memProvider;
            keyLen = keyfileBytes.length;
            log.warn("未找到 keyfile 资源，已自动生成临时 in-memory keyfile（仅用于开发测试）");
        }

        if (rsaPrivateKeyResource != null && rsaPrivateKeyResource.exists()) {
            try (var in = rsaPrivateKeyResource.getInputStream()) {
                rsaPem = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        } else {
            // 自动生成临时 RSA 密钥对
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair pair = gen.generateKeyPair();
            rsaPem = convertToPem(pair.getPrivate());
            log.warn("未找到 RSA 私钥资源，已自动生成临时密钥对（仅用于开发测试）");
        }

        imP2PMessageHandler.setKeyFileProvider(provider, keyId32, keyLen);
        imP2PMessageHandler.setCryptoParams(magic, version, 4, 5, maxFramePayload);
        imP2PMessageHandler.setRsaPrivateKeyPem(rsaPem);

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpServerCodec());
                        p.addLast(new HttpObjectAggregator(65536));
                        p.addLast(new ImP2PQueryParamHandler());
                        p.addLast(new WebSocketServerProtocolHandler(path, null, false));
                        p.addLast(imP2PMessageHandler);
                    }
                });

        ChannelFuture f = b.bind(port).sync();
        log.info("IM P2P WebSocket Server started at port={}, path={}", port, path);

        f.channel().closeFuture().addListener(cf -> {
            log.info("IM P2P WebSocket Server channel closed");
        });
    }

    @PreDestroy
    public void stop() {
        log.info("Shutting down IM P2P WebSocket Server...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    private String convertToPem(PrivateKey key) {
        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PRIVATE KEY-----\n");
        for (int i = 0; i < base64.length(); i += 64) {
            sb.append(base64, i, Math.min(i + 64, base64.length())).append('\n');
        }
        sb.append("-----END PRIVATE KEY-----\n");
        return sb.toString();
    }
}
