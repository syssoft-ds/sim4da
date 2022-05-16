package dev.oxoo2a.sim4da;

import java.util.HashMap;
import java.io.PrintStream;

public class Simulator {
    
    private final int n_nodes;
    private final Tracer tracer;
    private final Network network;
    private final HashMap<Integer, Node> nodes;
    
    public Simulator(int n_nodes, String name, boolean ordered, boolean enableTracing, boolean useLog4j2,
                     PrintStream alternativeDestination) {
        this.n_nodes = n_nodes;
        tracer = new Tracer(name, ordered, enableTracing, useLog4j2, alternativeDestination);
        network = new Network(n_nodes, tracer);
        nodes = new HashMap<>(n_nodes);
        for (int n_id = 0; n_id < n_nodes; n_id++)
            nodes.put(n_id, null);
    }
    
    public static Simulator createDefaultSimulator(int n_nodes) {
        return new Simulator(n_nodes, "sim4da", true, true, true, System.out);
    }
    
    public static Simulator createSimulator_Log4j2(int n_nodes) {
        return new Simulator(n_nodes,"sim4da", true, true, true, null);
    }
    
    public void attachNode(int id, Node node) {
        if (id>=0 && id<n_nodes)
            nodes.replace(id, node);
    }
    
    public void runSimulation(int duration) throws InstantiationException {
        // Check that all nodes are attached
        for (Node n : nodes.values()) {
            if (n==null) throw new InstantiationException();
            n.setNetwork(network);
            n.setTracer(tracer);
        }
        tracer.emit("Simulator::runSimulation with %d nodes for %d seconds", n_nodes, duration);
        nodes.values().forEach(Node::start);
        // Wait for the required duration
        try {
            Thread.sleep(duration * 1000L);
        } catch (InterruptedException ignored) {}
        
        // Stop network - release nodes waiting in receive ...
        network.stop();
        
        // Tell all nodes to stop and wait for the threads to terminate
        nodes.values().forEach(Node::stop);
        tracer.emit("Simulator::runSimulation finished");
    }
}
