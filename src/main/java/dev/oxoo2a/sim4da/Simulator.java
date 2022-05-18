package dev.oxoo2a.sim4da;

import java.io.PrintStream;
import java.util.Random;

import dev.oxoo2a.sim4da.Message.MessageType;

public class Simulator {
    
    private final Random random = new Random();
    private final Node[] nodes;
    private final Tracer tracer;
    private boolean stillSimulating = true;
    
    public Simulator(int numberOfNodes, String name, boolean orderedTracing, boolean useLog4j2,
                     PrintStream alternativeTracingDestination) {
        nodes = new Node[numberOfNodes];
        if (useLog4j2 || alternativeTracingDestination!=null)
            tracer = new Tracer(name, orderedTracing, useLog4j2, alternativeTracingDestination);
        else tracer = null; //no tracing
    }
    
    public static Simulator createDefaultSimulator(int numberOfNodes) {
        return new Simulator(numberOfNodes, "sim4da", true, true, System.out);
    }
    
    public static Simulator createSimulatorUsingLog4j2(int numberOfNodes) {
        return new Simulator(numberOfNodes, "sim4da", true, true, null);
    }
    
    public void attachNode(Node node) throws IllegalArgumentException {
        synchronized (nodes) { // Required for the check whether this node id already exists
            if (node.id>=0 && node.id<nodes.length) {
                if (nodes[node.id]==null) nodes[node.id]=node;
                else throw new IllegalArgumentException("Node with id "+node.id+" already exists in this simulator");
            } else throw new IllegalArgumentException("Node id out of range: "+node.id);
        }
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
        stillSimulating=false;
        for (Node node : nodes) { // Tell all nodes to stop and wait for the threads to terminate
            node.stop();
        }
        emitToTracer("Simulator::runSimulation finished");
    }
    
    public int getNumberOfNodes() {
        return nodes.length;
    }
    
    public Random getRandom() {
        return random;
    }
    
    public boolean isStillSimulating() {
        return stillSimulating;
    }
    
    public void sendUnicast(int senderId, int receiverId, String message) {
        if (receiverId<0 || receiverId>=nodes.length) {
            System.err.printf("Simulator::sendUnicast: unknown receiverId %d\n", receiverId);
            return;
        }
        if (senderId<0 || senderId>=nodes.length) {
            System.err.printf("Simulator::sendUnicast: unknown senderId %d\n", senderId);
            return;
        }
        emitToTracer("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, message);
        nodes[receiverId].putInMessageQueue(raw);
    }
    
    public void sendBroadcast(int senderId, String message) {
        if (senderId<0 || senderId>=nodes.length) {
            System.err.printf("Simulator::sendBroadcast: unknown senderId %d\n", senderId);
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
