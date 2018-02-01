package io.nuls.notify.entity;

import io.nuls.notify.utils.MurmurHash;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;

/**
 * @author daviyang35
 * @date 2018/1/30
 */
public class SubscriberDO {
    public SubscriberDO(WebSocket subscriberSock) {
        this.subscriberSock = subscriberSock;
        this.subscriberHash = SubscriberDO.genericHash(subscriberSock);
    }

    public int getSubscriberHash() {
        return subscriberHash;
    }

    private int subscriberHash;

    public WebSocket getSubscriberSock() {
        return subscriberSock;
    }

    public void setSubscriberSock(WebSocket subscriberSock) {
        this.subscriberSock = subscriberSock;
    }

    private WebSocket subscriberSock;

    public static Integer genericHash(WebSocket sock) {
        InetSocketAddress address = sock.getRemoteSocketAddress();
        String endpoint = address.toString();
        return MurmurHash.hash32(endpoint);
    }

}
