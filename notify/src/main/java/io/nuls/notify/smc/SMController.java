package io.nuls.notify.smc;

import io.nuls.notify.entity.SubscriberDO;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author daviyang35
 * @date 2018/1/30
 * Subscription Management Controller
 */
public class SMController {
    private Map<Integer, SubscriberDO> subscriberMap;

    public SMController() {
        subscriberMap = new HashMap<>(4);
    }

    public void addSubscriber(WebSocket sock) {
        SubscriberDO subscriberDO = new SubscriberDO(sock);
        subscriberMap.put(subscriberDO.getSubscriberHash(),subscriberDO);
    }

    public void removeSubscriber(WebSocket sock) {
        Integer key = SubscriberDO.genericHash(sock);
        subscriberMap.remove(key);
    }

    public void handleSubscriberMsg(WebSocket sock,String msg) {
        Integer key = SubscriberDO.genericHash(sock);
        SubscriberDO subscriberDO = subscriberMap.get(key);

        // decode Json Object
    }
}
