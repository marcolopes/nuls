package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.service.NetworkService;

import java.io.UnsupportedEncodingException;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    public ClientChannelHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        Node node = networkService.getNode(channel.remoteAddress().getHostString());
        //check node exist
        if(node != null && node.getStatus() != Node.WAIT) {
            channel.close();
            return;
        }
        NioChannelMap.add(channelId, channel);
        node.setChannelId(channelId);
        node.setStatus(Node.CONNECT);
        networkService.addNodeToGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP, node);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        Node node = networkService.getNode(channel.remoteAddress().getHostString());
        if(node != null) {
            node.destroy();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        String channelId = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String strMsg = new String(bytes, "UTF-8");
        System.out.println(channelId + ":" + strMsg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
