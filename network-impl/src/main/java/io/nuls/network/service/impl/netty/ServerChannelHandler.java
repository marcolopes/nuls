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

import java.net.InetAddress;

@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------- server  channelRegistered  ------------");
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        String remoteIP = socketChannel.remoteAddress().getHostString();
        System.out.println("-------------" + remoteIP);
        Node node = getNetworkService().getNode(remoteIP);
        //TODO pierre 直接关闭了连接, 双方同时对连，连接过程中，client程序加入了remote ip node，
        //TODO pierre server此处判断就存在了remoteip node
        if (node != null) {
            //若Node是连接状态，说明本机已和远程机建立了连接，就不需要再建立连接
            if(node.getStatus() == Node.CONNECT) {
                System.out.println("Node 状态: " + node.getStatus() + ", Server关闭连接. ");
                ctx.channel().close();
                return;
            } else {
                //若Node不是连接状态，并且Node类型是出站节点（当前channel的Node应作为入站节点），说明双方是互相发现的节点，会出现互相连接的情况
                //此时双方还未正式建立连接，根据IP规则，数值小的IP作为Server，大的作为Client
                if(node.getType() == Node.OUT) {
                    String localIP = InetAddress.getLocalHost().getHostAddress();
                    boolean isLocalServer = judgeIsLocalServer(localIP, remoteIP);
                    //本机不能作为server，关闭连接
                    if(!isLocalServer) {
                        System.out.println("本地-"+localIP+"-不能作为Server, 远程IP: " + remoteIP);
                        ctx.channel().close();
                        return;
                    } else {
                        //本机应作为Server，需remove出站类型的Node，在channelActive中add入站类型的Node
                        getNetworkService().removeNode(remoteIP);
                        NodeGroup nodeGroup = getNetworkService().getNodeGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP);
                        nodeGroup.removeNode(node);
                    }
                }
            }
        }
        NodeGroup group = getNetworkService().getNodeGroup(NetworkConstant.NETWORK_NODE_IN_GROUP);
        if (group.size() > getNetworkService().getNetworkParam().maxInCount()) {
            ctx.channel().close();
            return;
        }
    }

    boolean judgeIsLocalServer(String localIP, String remoteIP) {
        long local = ipToLong(localIP);
        long remote = ipToLong(remoteIP);
        if(local < remote)
            return true;
        return false;
    }

    long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressInArray = ipAddress.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            //left shifting 24,16,8,0 and bitwise OR
            //1. 192 << 24
            //1. 168 << 16
            //1. 1   << 8
            //1. 2   << 0
            result |= ip << (i * 8);
        }
        return result;
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
