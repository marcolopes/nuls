package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.service.NetworkService;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    public ClientChannelHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------- client  channelRegistered  ------------");
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();

        Node node = getNetworkService().getNode(channel.remoteAddress().getHostString());
        //check node exist
        if(node != null && node.getStatus() != Node.WAIT) {
            channel.close();
            return;
        }
        NioChannelMap.add(channelId, channel);
        node.setChannelId(channelId);
        node.setStatus(Node.CONNECT);
        getNetworkService().addNodeToGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP, node);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------- client  channelInactive  ------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        Node node = getNetworkService().getNode(channel.remoteAddress().getHostString());
        if(node != null) {
            node.destroy();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        System.out.println("---------- client  channelRead  ------------");
        String channelId = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("---------- client  channelReadComplete  ------------");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("---------- client  exceptionCaught  ------------");
        cause.printStackTrace();
        ctx.channel().close();
    }

    private NetworkService getNetworkService() {
        if(networkService == null) {
            networkService = NulsContext.getServiceBean(NetworkService.class);
        }
        return networkService;
    }

}
