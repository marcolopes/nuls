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


import io.nuls.consensus.constant.PocConsensusConstant;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.thread.manager.TaskManager;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.entity.NodeGroup;
import io.nuls.network.entity.param.AbstractNetworkParam;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vivi
 * @date 2017/11/21
 */
public class NettyNodesManager implements Runnable {

    private Map<String, NodeGroup> nodeGroups = new ConcurrentHashMap<>();

    private Map<String, Node> nodes = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private List<Node> seedNodes;

    private AbstractNetworkParam network;

    private NodeDiscoverHandler discoverHandler;

    private NettyConnectionManager connectionManager;

    private boolean running;

    public NettyNodesManager(AbstractNetworkParam network, NettyConnectionManager connectionManager) {
        this.network = network;
        this.connectionManager = connectionManager;
        // init default NodeGroup
        NodeGroup inNodes = new NodeGroup(NetworkConstant.NETWORK_NODE_IN_GROUP);
        NodeGroup outNodes = new NodeGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP);
        nodeGroups.put(inNodes.getName(), inNodes);
        nodeGroups.put(outNodes.getName(), outNodes);

        discoverHandler = new NodeDiscoverHandler(this, network, null);
    }

    /**
     * Check if is a consensus node，add consensusNodeGroup
     */
    public void init() {
        boolean isConsensus = NulsContext.MODULES_CONFIG.getCfgValue(PocConsensusConstant.CFG_CONSENSUS_SECTION, PocConsensusConstant.PROPERTY_PARTAKE_PACKING, false);
        if (isConsensus) {
            NodeGroup consensusNodes = new NodeGroup(NetworkConstant.NETWORK_NODE_CONSENSUS_GROUP);
            nodeGroups.put(consensusNodes.getName(), consensusNodes);
        }
    }

    /**
     * get nodes from database
     * connect other nodes
     * running ping/pong thread
     * running node discovery thread
     */
    public void start() {
        List<Node> nodes = getSeedNodes();
        for (Node node : nodes) {
            node.setType(Node.OUT);
            node.setStatus(Node.WAIT);
            addNodeToGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP, node);
        }
        running = true;
        TaskManager.createAndRunThread(NulsConstant.MODULE_ID_NETWORK, "connectionManager", this);
    }

    public List<Node> getSeedNodes() {
        if (seedNodes == null) {
            seedNodes = discoverHandler.getSeedNodes();
        }
        return seedNodes;
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public boolean containsNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }

    public void addNode(Node node) {
        lock.lock();
        try {
            if (!nodes.containsKey(node.getIp())) {
                nodes.put(node.getIp(), node);
            }
            if (node.getStatus() == Node.WAIT) {
                connectionManager.connectionNode(node);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeNode(String nodeId) {
        if (!nodes.containsKey(nodeId)) {
            nodes.remove(nodeId);
        }
    }

    public void addNodeToGroup(String groupName, Node node) {
        if (!nodeGroups.containsKey(groupName)) {
            throw new NulsRuntimeException(ErrorCode.NET_NODE_GROUP_NOT_FOUND);
        }
        NodeGroup group = nodeGroups.get(groupName);
        if (groupName.equals(NetworkConstant.NETWORK_NODE_OUT_GROUP) &&
                group.size() >= network.maxOutCount()) {
            return;
        }

        if (groupName.equals(NetworkConstant.NETWORK_NODE_IN_GROUP) &&
                group.size() >= network.maxInCount()) {
            return;
        }
        node.getGroupSet().add(group.getName());
        addNode(node);
        group.addNode(node);
    }


    /**
     * check connecting node enough
     */
    @Override
    public void run() {
        while (running) {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            for (Node node : nodes.values()) {
                System.out.println("-------------ip:" + node.getIp() + "-------status:" + node.getStatus());
                if (node.getStatus() == Node.CLOSE) {
                    for (String groupName : node.getGroupSet()) {
                        NodeGroup group = nodeGroups.get(groupName);
                        if (group != null) {
                            group.removeNode(node);
                        }
                        nodes.remove(node.getIp());
                    }
                    node = null;
                }
            }
            if (nodes.isEmpty()) {
                List<Node> nodes = getSeedNodes();
                for (Node node : nodes) {
                    node.setType(Node.OUT);
                    node.setStatus(Node.WAIT);
                    addNodeToGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP, node);
                }
            }
            System.out.println("------------华丽的分割线----------------");

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }
}
