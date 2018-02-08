package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.context.NulsContext;
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
        System.out.println("---------- server  channelRegistered  ------------");
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        System.out.println("-------------" + socketChannel.remoteAddress().getHostString());
        if (getNetworkService().containsNode(socketChannel.remoteAddress().getHostString())) {
            ctx.channel().close();
            return;
        }
        NodeGroup group = getNetworkService().getNodeGroup(NetworkConstant.NETWORK_NODE_IN_GROUP);
        if (group.size() > getNetworkService().getNetworkParam().maxInCount()) {
            ctx.channel().close();
            return;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------- server  channelActive  ------------");
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        NioChannelMap.add(channelId, channel);
        Node node = new Node(getNetworkService().getNetworkParam(), Node.IN, channel.remoteAddress().getHostString(), channel.remoteAddress().getPort(), channelId);
        node.setStatus(Node.CONNECT);
        getNetworkService().addNodeToGroup(NetworkConstant.NETWORK_NODE_IN_GROUP, node);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------- server  channelInactive  ------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        String channelId = ctx.channel().id().asLongText();
        NioChannelMap.remove(channelId);
        Node node = getNetworkService().getNode(channel.remoteAddress().getHostString());
        if (node != null && channelId.equals(node.getChannelId())) {
            node.destroy();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("----  service exceptionCaught() ------------");
        cause.printStackTrace();
        ctx.channel().close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----  service channelReadComplete() ------------");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("----  service channelRead() ------------");
        String channelId = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String strMsg = new String(bytes, "UTF-8");
        System.out.println(channelId + ":" + strMsg);
    }

    private NetworkService getNetworkService() {
        if (networkService == null) {
            networkService = NulsContext.getServiceBean(NetworkService.class);
        }
        return networkService;
    }
}
