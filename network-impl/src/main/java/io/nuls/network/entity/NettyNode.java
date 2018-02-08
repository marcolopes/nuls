package io.nuls.network.entity;

import java.util.Set;

public class NettyNode extends Node {

    private Set<NodeGroup> groupSet;

    /**
     * 1: inNode ,  2: outNode
     */
    public final static int IN = 1;
    public final static int OUT = 2;
    private int type;

    /**
     * 0: wait , 1: connecting, 2: handshake 3: close
     */
    public final static int WAIT = 0;
    public final static int CONNECTING = 1;
    public final static int HANDSHAKE = 2;
    public final static int CLOSE = 3;

    private volatile int status;

}
