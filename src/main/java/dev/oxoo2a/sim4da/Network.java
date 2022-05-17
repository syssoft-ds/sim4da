package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Message.MessageType;

public class Network {
    
    private final int numberOfNodes;
    private final Tracer tracer;
    private final Node[] nodes;
    
    public Network(int numberOfNodes, Node[] nodes, Tracer tracer) {
        this.numberOfNodes = numberOfNodes;
        this.nodes = nodes;
        this.tracer = tracer;
    }
    
    public int getNumberOfNodes() {
        return numberOfNodes;
    }
    
    public void unicast(int senderId, int receiverId, String message) {
        if (receiverId<0 || receiverId>=numberOfNodes) {
            System.err.printf("Network::unicast: unknown receiver id %d\n", receiverId);
            return;
        }
        if (senderId<0 || senderId>=numberOfNodes) {
            System.err.printf("Network::unicast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, message);
        nodes[receiverId].putInMessageQueue(raw);
    }
    
    public void broadcast(int senderId, String message) {
        if (senderId<0 || senderId>=numberOfNodes) {
            System.err.printf("Network::broadcast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Broadcast:%d->0..%d", senderId, numberOfNodes-1);
        Message raw = new Message(senderId, -1, MessageType.BROADCAST, message);
        for (int l = 0; l<numberOfNodes; l++) {
            if (l==senderId) continue;
            raw.receiverId = l;
            nodes[l].putInMessageQueue(raw);
        }
    }
    
    public Message receive(int receiverId) {
        if (receiverId<0 || receiverId>=numberOfNodes) {
            System.err.printf("Network::receive: unknown receiver id %d\n", receiverId);
            return null;
        }
        Message m = nodes[receiverId].awaitFromMessageQueue();
        if (m!=null) {
            String m_type = m.type==MessageType.BROADCAST ? "Broadcast" : "Unicast";
            tracer.emit("Receive %s:%d<-%d", m_type, m.receiverId, m.senderId);
        }
        return m;
    }
}
