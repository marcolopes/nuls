package io.nuls.notify.websocket;

import io.nuls.notify.smc.SMController;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * @author daviyang35
 * @date 2018/1/19
 */
public class NotifyServer extends WebSocketServer {
    public SMController getSmController() {
        return smController;
    }

    public void setSmController(SMController smController) {
        this.smController = smController;
    }

    private SMController smController;

    public NotifyServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        if (smController != null) {
            smController.addSubscriber(webSocket);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        if (smController != null) {
            smController.removeSubscriber(webSocket);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (smController != null) {
            smController.handleSubscriberMsg(webSocket, s);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        if (smController != null) {
            smController.removeSubscriber(webSocket);
        }
    }

    @Override
    public void onStart() {

    }
}
