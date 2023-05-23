package dev.oxoo2a.sim4da;

import java.util.HashMap;
import java.io.PrintStream;

public class Simulator implements Node2Simulator {

    public static Simulator createDefaultSimulator ( int n_nodes, Clock clock ) {
        return new Simulator(n_nodes, "sim4da", true, true, true, System.out, clock);
    }

    public static Simulator createSimulator_Log4j2 ( int n_nodes, Clock clock ) {
        return new Simulator(n_nodes,"sim4da", true,true,true,null, clock);
    }

    public Simulator ( int n_nodes, String name, boolean ordered, boolean enableTracing, boolean useLog4j2, PrintStream alternativeDestination, Clock clock) {
        this.n_nodes = n_nodes;
        tracer = new Tracer(name,ordered,enableTracing,useLog4j2,alternativeDestination);
        network = new Network(n_nodes,tracer, clock);
        nodes = new HashMap<Integer, Simulator2Node>(n_nodes);
        // this.clock = clock; //Lamport Clock
        this.clock = null; // Vector Clock
        for (int n_id = 0; n_id < n_nodes; ++n_id)
            nodes.put(n_id, null);
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock; //setting instance for Vector Clock implementation

    }
    Clock getClock() {
        return clock;
    }

    @Override
    public int numberOfNodes() {
        return n_nodes;
    }

    public void attachNode (int id, Simulator2Node node ) {
        if ((0 <= id) && (id < n_nodes))
            nodes.replace(id,node);
    }
    public void setNetwork(Network network) {
        this.network = network;
    }
    public void runSimulation ( int duration ) throws InstantiationException {
        // Check that all nodes are attached
        for ( Simulator2Node n : nodes.values() ) {
            if (n == null) throw new InstantiationException();
            n.setSimulator(this);
        }

        tracer.emit("Simulator::runSimulation with %d nodes for %d seconds",n_nodes,duration);
        is_simulating = true;
        nodes.values().forEach(Simulator2Node::start);
        // Wait for the required duration
        try {
            Thread.sleep(duration * 1000L);
        }
        catch (InterruptedException ignored) {}
        is_simulating = false;

        // Stop network - release nodes waiting in receive ...
        network.stop();

        // Tell all nodes to stop and wait for the threads to terminate
        nodes.values().forEach(Simulator2Node::stop);
        tracer.emit("Simulator::runSimulation finished");
    }

    @Override
    public boolean stillSimulating() {
        return is_simulating;
    }

    @Override
    public void sendUnicast(int sender_id, int receiver_id, String m) {
        network.unicast(sender_id,receiver_id,m);
    }

    @Override
    public void sendUnicast ( int sender_id, int receiver_id, Message m ) {
        network.unicast(sender_id,receiver_id,m.toJson());
    }

    @Override
    public void sendBroadcast ( int sender_id, String m ) {
        network.broadcast(sender_id,m);
    }

    @Override
    public void sendBroadcast ( int sender_id, Message m ) {
        network.broadcast(sender_id,m.toJson());
    }

    @Override
    public Network.Message receive ( int receiver_id ) {
        return network.receive(receiver_id);
    }

    @Override
    public void emit ( String format, Object ... args ) {
        tracer.emit(format,args);
    }

    private final int n_nodes;
    private final Tracer tracer;
    private Network network;
    private final HashMap<Integer, Simulator2Node> nodes;
    private Clock clock;
    private boolean is_simulating = false;
}
