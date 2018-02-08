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
import io.nuls.core.thread.manager.TaskManager;
import io.nuls.network.entity.Node;
import io.nuls.network.entity.param.AbstractNetworkParam;
import io.nuls.network.service.impl.netty.NettyClient;
import io.nuls.network.service.impl.netty.NettyServer;

/**
 * @author vivi
 * @date 2017-11-10
 */
public class NettyConnectionManager {

    private AbstractNetworkParam network;
    private NettyServer nettyServer;

    public NettyConnectionManager(AbstractNetworkParam network) {
        this.network = network;
    }

    public void init() {
        nettyServer = new NettyServer(network.port());
        nettyServer.init();
    }

    public void start() throws InterruptedException {
//        TaskManager.createAndRunThread(NulsConstant.MODULE_ID_NETWORK, "node connection", new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    nettyServer.start();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, false);

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

    public void processMessage(Byte[] msg, Node node) {

    }
}
