package dev.oxoo2a.sim4da;

import java.io.PrintStream;

import dev.oxoo2a.sim4da.Message.MessageType;

public class Simulator {
    
    private final Tracer tracer;
    private final Node[] nodes;
    
    public Simulator(int numberOfNodes, String name, boolean ordered, boolean useLog4j2,
                     PrintStream alternativeDestination) {
        if (useLog4j2 || alternativeDestination!=null)
            tracer = new Tracer(name, ordered, useLog4j2, alternativeDestination);
        else tracer = null; //no tracing
        nodes = new Node[numberOfNodes];
    }
    
    public static Simulator createDefaultSimulator(int numberOfNodes) {
        return new Simulator(numberOfNodes, "sim4da", true, true, System.out);
    }
    
    public static Simulator createSimulatorUsingLog4j2(int numberOfNodes) {
        return new Simulator(numberOfNodes, "sim4da", true, true, null);
    }
    
    public void attachNode(int id, Node node) {
        if (id>=0 && id<nodes.length) nodes[id]=node;
    }
    
    public void runSimulation(int duration) throws InstantiationException {
        // Check that all nodes are attached
        for (Node node : nodes) {
            if (node==null) throw new InstantiationException();
        }
        emitToTracer("Simulator::runSimulation with %d nodes for %d seconds", nodes.length, duration);
        for (Node node : nodes) {
            node.start();
        }
        try {
            Thread.sleep(duration * 1000L); // Wait for the required duration
        } catch (InterruptedException ignored) {}
        for (Node node : nodes) { // Tell all nodes to stop and wait for the threads to terminate
            node.stop();
        }
        emitToTracer("Simulator::runSimulation finished");
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
        emitToTracer("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, message);
        nodes[receiverId].putInMessageQueue(raw);
    }
    
    public void broadcast(int senderId, String message) {
        if (senderId<0 || senderId>=nodes.length) {
            System.err.printf("Network::broadcast: unknown sender id %d\n", senderId);
            return;
        }
        emitToTracer("Broadcast:%d->0..%d", senderId, nodes.length-1);
        Message raw = new Message(senderId, -1, MessageType.BROADCAST, message);
        for (int i = 0; i<nodes.length; i++) {
            if (i==senderId) continue;
            raw.receiverId = i; //TODO This is most probably not correct.
                                // Since there is only a single Message object whose receiverId is overwritten
                                // i times, all nodes will see receiverId==i-1 after the loop.
            nodes[i].putInMessageQueue(raw);
        }
    }
    
    public void emitToTracer(String format, Object... args) {
        if (tracer!=null) tracer.emit(format, args);
    }
}
