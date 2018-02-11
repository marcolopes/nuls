/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.network.service.impl;

import io.nuls.core.constant.NulsConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.event.BaseEvent;
import io.nuls.core.event.EventManager;
import io.nuls.core.exception.NulsException;
import io.nuls.core.mesasge.NulsMessage;
import io.nuls.core.thread.manager.TaskManager;
import io.nuls.event.bus.service.intf.EventBusService;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.entity.param.AbstractNetworkParam;
import io.nuls.network.message.NetworkEventHandlerFactory;
import io.nuls.network.message.NetworkEventResult;
import io.nuls.network.message.handler.NetWorkEventHandler;
import io.nuls.network.service.NetworkService;
import io.nuls.network.service.impl.netty.NettyClient;
import io.nuls.network.service.impl.netty.NettyServer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author vivi
 * @date 2017-11-10
 */
public class NettyConnectionManager {

    private AbstractNetworkParam network;
    private NetworkService networkService;
    private NettyServer nettyServer;
    private EventBusService eventBusService;
    private NetworkEventHandlerFactory messageHandlerFactory;


    public NettyConnectionManager(AbstractNetworkParam network, NetworkService networkService) {
        this.network = network;
        this.networkService = networkService;
    }

    public void init() {
        nettyServer = new NettyServer(network.port());
        nettyServer.init();
//        eventBusService = NulsContext.getServiceBean(EventBusService.class);
        messageHandlerFactory = network.getMessageHandlerFactory();
    }

    public void start() throws InterruptedException {
        TaskManager.createAndRunThread(NulsConstant.MODULE_ID_NETWORK, "node connection", new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, false);

    }

    public void connectionNode(Node node) {
        TaskManager.createAndRunThread(NulsConstant.MODULE_ID_NETWORK, "node connection", new Runnable() {
            @Override
            public void run() {
                NettyClient client = new NettyClient(node);
                client.start();
            }
        }, false);
    }

    public void receiveMessage(ByteBuffer buffer, Node node) {
        buffer.flip();
        try {
            NulsMessage message = new NulsMessage(buffer);
            BaseEvent event = EventManager.getInstance(message.getData());
            processMessage(event, node);
        } catch (NulsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return;
        }
    }

    private void processMessage(BaseEvent event, Node node) {
        if (isNetworkEvent(event)) {
            if (node.getStatus() != Node.HANDSHAKE && !isHandShakeMessage(event)) {
                return;
            }
            asynExecute(event, node);
        } else {
            if (!node.isHandShake()) {
                return;
            }
            eventBusService.publishNetworkEvent(event, node.getId());
        }
    }

    private void asynExecute(BaseEvent networkEvent, Node node) {
        NetWorkEventHandler handler = messageHandlerFactory.getHandler(networkEvent);
        TaskManager.asynExecuteRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    NetworkEventResult messageResult = handler.process(networkEvent, node);
                    processMessageResult(messageResult, node);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void processMessageResult(NetworkEventResult eventResult, Node node) throws IOException {
        if (node.getStatus() == Node.CLOSE) {
            return;
        }
        if (eventResult == null || !eventResult.isSuccess()) {
            return;
        }
        if (eventResult.getReplyMessage() != null) {
            networkService.sendToNode(eventResult.getReplyMessage(), node.getId(), true);
        }
    }

    private boolean isNetworkEvent(BaseEvent event) {
        return event.getHeader().getModuleId() == NulsConstant.MODULE_ID_NETWORK;
    }

    private boolean isHandShakeMessage(BaseEvent event) {
        if (isNetworkEvent(event)) {
            if (event.getHeader().getEventType() == NetworkConstant.NETWORK_GET_VERSION_EVENT
                    || event.getHeader().getEventType() == NetworkConstant.NETWORK_VERSION_EVENT) {
                return true;
            }
        }
        return false;
    }
}
