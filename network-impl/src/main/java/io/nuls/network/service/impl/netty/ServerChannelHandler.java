package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.entity.NodeGroup;
import io.nuls.network.service.NetworkService;

@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        NodeGroup group = networkService.getNodeGroup(NetworkConstant.NETWORK_NODE_IN_GROUP);
        if (group.size() > networkService.getNetworkParam().maxInCount()) {
            ctx.channel().close();
        }
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        if (networkService.containsNode(socketChannel.remoteAddress().getAddress().getHostAddress())) {
            ctx.channel().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        NioChannelMap.add(channelId, channel);
        Node node = new Node(networkService.getNetworkParam(), Node.IN, channel.remoteAddress().getHostName(), channel.remoteAddress().getPort(), channelId);
        networkService.addNodeToGroup(NetworkConstant.NETWORK_NODE_IN_GROUP, node);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        NioChannelMap.remove(channelId);
        networkService.removeNode(channelId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("----  service exceptionCaught() ------------");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String strMsg = new String(bytes, "UTF-8");
        System.out.println(channelId + ":" + strMsg);
    }
}
