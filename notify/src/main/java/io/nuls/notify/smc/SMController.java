package io.nuls.notify.smc;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author daviyang35
 * @date 2018/1/30
 * Subscription Management Controller
 */
public class SMController {
    private List<WebSocket> subscriberArray;

    public SMController() {
        subscriberArray = new ArrayList<>(4);
    }

    public void addSubscriber(WebSocket sock) {
        subscriberArray.add(sock);
    }

    public void removeSubscriber(WebSocket sock) {
        subscriberArray.remove(sock);
    }

    public void handleSubscriberMsg(WebSocket sock, String msg) {
        // decode Json Object
        // nop
    }

    public void removeAllSubscriber() {
        for (WebSocket client : subscriberArray) {
            client.close();
        }
        subscriberArray.clear();
    }

    private void process() {

    }
}
