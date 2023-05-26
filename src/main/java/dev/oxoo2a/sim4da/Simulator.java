package dev.oxoo2a.sim4da;

import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintStream;

public class Simulator implements Node2Simulator {

    public static Simulator createDefaultSimulator(int n_nodes) {
        Simulator simulator = new Simulator(n_nodes, "sim4da", true, true, true, System.out);
        for (int id = 0; id < n_nodes; id++) {
            VectorClock clock = new VectorClock(n_nodes, id, 0);
            simulator.attachNode(id, new TokenRingNode(id, clock, simulator.getTracer()));
            simulator.setClock(clock);
        }
        return simulator;
    }

    public static Simulator createSimulator_Log4j2(int n_nodes) {
        return new Simulator(n_nodes, "sim4da", true, true, true, null);
    }

    public Simulator(int n_nodes, String name, boolean ordered, boolean enableTracing, boolean useLog4j2, PrintStream alternativeDestination) {
        this.n_nodes = n_nodes;
        tracer = new Tracer(name, ordered, enableTracing, useLog4j2, alternativeDestination);
        network = new Network(n_nodes, tracer, clock);
        nodes = new HashMap<Integer, Simulator2Node>(n_nodes);
        for (int n_id = 0; n_id < n_nodes; ++n_id)
            nodes.put(n_id, null);
    }

    @Override
    public void setClock(VectorClock clock) {
        this.clock = clock; //setting instance for Vector Clock implementation
        emit("Clock type set to: %s", clock.getClass().getSimpleName()); //logging statement to display clock type
    }

    public Clock getClock() {
        return clock;
    }

    @Override
    public int numberOfNodes() {
        return n_nodes;
    }

    public void attachNode(int id, Simulator2Node node) {
        if ((0 <= id) && (id < n_nodes))
            nodes.replace(id, node);

        VectorClock clock = (VectorClock) this.clock; // Cast the clock to VectorClock
        node.setClock(clock);


    }

    public void setNetwork(Network network) {
        this.network = network;
        network.setClock(clock);
    }

    public void runSimulation(int duration) throws InstantiationException {
        // Check that all nodes are attached
        for (Simulator2Node n : nodes.values()) {
            if (n == null) {
                throw new InstantiationException("Node not attached.");
            }

            n.setSimulator(this);

        }

        tracer.emit("Simulator::runSimulation with %d nodes for %d seconds",n_nodes,duration);
        emit("Initial clock values: %s", Arrays.toString(clock.getValues())); //Logging statement to display initial clock value

        is_simulating = true;
        nodes.values().forEach(Simulator2Node::start);
        // Wait for the required duration
        try {
            Thread.sleep(duration * 1000L);
        }
        catch (InterruptedException ignored) {}
        is_simulating = false;

        emit("Final clock values: %s", Arrays.toString(clock.getValues())); //logging statement to display final clock values

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

        //emit("Clock values: %s", Arrays.toString(clock.getValues())); //logging statement to display clock values
    }

    public Tracer getTracer(){
        return tracer;
    }
    private final int n_nodes;
    private final Tracer tracer;
    private Network network;
    private final HashMap<Integer, Simulator2Node> nodes;

    private VectorClock clock;
    private boolean is_simulating = false;
}

