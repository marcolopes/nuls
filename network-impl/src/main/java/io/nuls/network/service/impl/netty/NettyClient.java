package io.nuls.network.service.impl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.nuls.core.utils.log.Log;
import io.nuls.network.entity.Node;


public class NettyClient {

    public static EventLoopGroup worker = new NioEventLoopGroup();

    Bootstrap boot;

    private SocketChannel socketChannel;

    private Node node;

    public NettyClient(Node node) {
        this.node = node;
        boot = new Bootstrap();
        boot.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new NulsChannelInitializer<>(new ClientChannelHandler()));

    }

    public void start() {
        try {
            ChannelFuture future = boot.connect(node.getIp(), node.getPort()).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
            } else {
                node.destroy();
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Log.error(e);
            socketChannel.close();
            node.destroy();
        }
    }
}
