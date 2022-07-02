package dev.oxoo2a.sim4da;

import java.io.PrintStream;

import dev.oxoo2a.sim4da.LogicalTimestamp.ExtendedLamportTimestamp;
import dev.oxoo2a.sim4da.LogicalTimestamp.VectorTimestamp;
import dev.oxoo2a.sim4da.Message.MessageType;

public class Simulator {
    
    private final Node[] nodes;
    private final Tracer tracer;
    private final TimestampType timestampType;
    private final int maxMessageLatency;
    private final boolean traceMessages;
    
    // This is only changed to false once in the main thread, but still needs to be volatile
    // to ensure that all Node threads can actually see that change.
    private volatile boolean stillSimulating = true;
    
    public Simulator(int numberOfNodes, TimestampType timestampType, int maxMessageLatency) { // without tracing
        this(numberOfNodes, timestampType, maxMessageLatency, null, false, null, false);
    }
    
    public Simulator(int numberOfNodes, TimestampType timestampType, int maxMessageLatency, String tracerName,
                     boolean useLog4j2, PrintStream alternativeTracingDestination, boolean traceMessages) {
        nodes = new Node[numberOfNodes];
        if (useLog4j2 || alternativeTracingDestination!=null)
            tracer = new Tracer(tracerName, false, useLog4j2, alternativeTracingDestination);
        else tracer = null; //no tracing
        this.timestampType = timestampType;
        this.maxMessageLatency = maxMessageLatency;
        this.traceMessages = traceMessages;
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
        emitToTracer("Simulator::runSimulation with %d nodes for %d ms", nodes.length, duration);
        for (Node node : nodes) {
            node.start();
        }
        try {
            Thread.sleep(duration); // Wait for the required duration
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
    
    public int getMaxMessageLatency() {
        return maxMessageLatency;
    }
    
    public boolean isTraceMessages() {
        return traceMessages;
    }
    
    public boolean isStillSimulating() {
        return stillSimulating;
    }
    
    public void sendUnicast(int senderId, int receiverId, LogicalTimestamp timestamp, String payload) {
        if (receiverId<0 || receiverId>=nodes.length) {
            emitToTracer("Simulator::sendUnicast: unknown receiverId %d", receiverId);
            return;
        }
        if (senderId<0 || senderId>=nodes.length) {
            emitToTracer("Simulator::sendUnicast: unknown senderId %d", senderId);
            return;
        }
        if (traceMessages) emitToTracer("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, timestamp, payload);
        nodes[receiverId].putInMessageQueue(raw);
    }
    
    public void sendBroadcast(int senderId, LogicalTimestamp timestamp, String payload) {
        if (senderId<0 || senderId>=nodes.length) {
            emitToTracer("Simulator::sendBroadcast: unknown senderId %d", senderId);
            return;
        }
        if (traceMessages) emitToTracer("Broadcast:%d->0..%d", senderId, nodes.length-1);
        Message raw = new Message(senderId, -1, MessageType.BROADCAST, timestamp, payload);
        for (int i = 0; i<nodes.length; i++) {
            if (i==senderId) continue; //don't send broadcast back to sender
            nodes[i].putInMessageQueue(new Message(raw.getSenderId(), i, raw.getType(),
                    raw.getTimestamp(), raw.getPayload()));
        }
    }
    
    public void emitToTracer(String format, Object... args) {
        if (tracer!=null) tracer.emit(format, args);
    }
    
    LogicalTimestamp getInitialTimestamp(int nodeId) {
        switch (timestampType) {
            case NONE:
                return null;
            case EXTENDED_LAMPORT:
                return new ExtendedLamportTimestamp(nodeId, 0L);
            case VECTOR:
                return new VectorTimestamp(nodes.length, 0L);
        }
        return null; // don't use any timestamps during simulation
    }
    
    public enum TimestampType {
        NONE, EXTENDED_LAMPORT, VECTOR
    }
}
