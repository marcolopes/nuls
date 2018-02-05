package io.nuls.notify.module;

import io.nuls.core.context.NulsContext;
import io.nuls.core.event.BaseEvent;
import io.nuls.core.module.BaseModuleBootstrap;
import io.nuls.core.utils.log.Log;
import io.nuls.event.bus.service.intf.EventBusService;
import io.nuls.notify.handler.EventsHandler;
import io.nuls.notify.smc.SMController;
import io.nuls.notify.websocket.NotifyServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author daviyang35
 * @date 2018/1/19
 */
public class NotifyModule extends BaseModuleBootstrap {
    private EventBusService eventBusService;
    private NotifyServer notifyServer;
    private SMController smController;
    short port;

    public NotifyModule() {
        super((short) 10);
    }

    @Override
    public void init() {
        Log.debug("Init");
        // read property
        eventBusService = NulsContext.getServiceBean(EventBusService.class);
        eventBusService.subscribeEvent(BaseEvent.class, new EventsHandler());
        String portString = getModuleCfgProperty("notify", "notify.port");
        if (portString != null) {
            port = Short.parseShort(portString);
        } else {
            port = 8632;
        }

        // register all events
    }

    @Override
    public void start() {
        Log.debug("Start");
        // running web socket server
        notifyServer = new NotifyServer(new InetSocketAddress("127.0.0.1", port));
        notifyServer.setSmController(new SMController());
        notifyServer.setReuseAddr(true);
        notifyServer.setTcpNoDelay(true);
        notifyServer.start();
    }

    @Override
    public void shutdown() {
        Log.debug("Shutdown");
        // stop web socket server
        smController.removeAllSubscriber();
        notifyServer.setSmController(null);
        try {
            notifyServer.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        Log.debug("Destroy");
        smController = null;
        notifyServer = null;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
