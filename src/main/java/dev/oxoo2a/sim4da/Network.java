package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Message.MessageType;

public class Network {
    
    private final Tracer tracer;
    private final Node[] nodes;
    
    public Network(Node[] nodes, Tracer tracer) {
        this.nodes = nodes;
        this.tracer = tracer;
    }
    
    public int getNumberOfNodes() {
        return nodes.length;
    }
    
    public void unicast(int senderId, int receiverId, String message) {
        if (receiverId<0 || receiverId>=nodes.length) {
            System.err.printf("Network::unicast: unknown receiver id %d\n", receiverId);
            return;
        }
        if (senderId<0 || senderId>=nodes.length) {
            System.err.printf("Network::unicast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, message);
        nodes[receiverId].putInMessageQueue(raw);
    }
    
    public void broadcast(int senderId, String message) {
        if (senderId<0 || senderId>=nodes.length) {
            System.err.printf("Network::broadcast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Broadcast:%d->0..%d", senderId, nodes.length-1);
        Message raw = new Message(senderId, -1, MessageType.BROADCAST, message);
        for (int i = 0; i<nodes.length; i++) {
            if (i==senderId) continue;
            raw.receiverId = i; //TODO This is most probably not correct.
                                // Since there is only a single Message object whose receiverId is overwritten
                                // i times, all nodes will see receiverId==i-1 after the loop.
            nodes[i].putInMessageQueue(raw);
        }
    }
}
