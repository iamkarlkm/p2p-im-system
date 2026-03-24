package com.im.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrameCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket服务器
 */
@Component
public class NettyWebSocketServer {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyWebSocketServer.class);
    
    @Value("${netty.websocket.port:9000}")
    private int port;
    
    @Value("${netty.websocket.boss-threads:4}")
    private int bossThreads;
    
    @Value("${netty.websocket.worker-threads:8}")
    private int workerThreads;
    
    @Value("${netty.websocket.max-connections:10000}")
    private int maxConnections;
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    
    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                initServer();
            } catch (Exception e) {
                logger.error("Netty服务器启动失败", e);
            }
        }).start();
    }
    
    private void initServer() throws Exception {
        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // HTTP编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 聚合HTTP请求
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        // 心跳检测
                        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));
                        // WebSocket协议处理器
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true));
                        // WebSocket消息编解码
                        pipeline.addLast(new TextWebSocketFrameCodec());
                        // WebSocket消息处理器
                        pipeline.addLast(new WebSocketMessageHandler());
                    }
                });
        
        ChannelFuture future = bootstrap.bind(port).sync();
        serverChannel = future.channel();
        logger.info("Netty WebSocket服务器启动成功，监听端口: {}", port);
        
        serverChannel.closeFuture().sync();
    }
    
    @PreDestroy
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        logger.info("Netty WebSocket服务器已关闭");
    }
}
