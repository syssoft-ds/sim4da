package dev.oxoo2a.sim4da;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.oxoo2a.sim4da.Network;

public class Simulator {

    public Simulator ( int n_nodes ) {
        this.n_nodes = n_nodes;

        network = new Network(n_nodes);
        nodes = new HashMap<Integer, Node>(n_nodes);
        for (int n_id = 0; n_id < n_nodes; ++n_id)
            nodes.put(n_id, null);
    }

    public void attachNode ( int id, Node node ) {
        if ((0 <= id) && (id < n_nodes))
            nodes.replace(id,node);
    }

    public void runSimulation ( int duration ) throws InstantiationException {
        // Check that all nodes are attached
        for ( Node n : nodes.values() ) {
            if (n == null) throw new InstantiationException();
            n.setNetwork(network);
        }
        logger.info("Simulator::runSimulation with "+n_nodes+" nodes for "+duration+" seconds");
        nodes.values().forEach(Node::start);
        // Wait for the required duration
        try {
            Thread.sleep(duration * 1000L);
        }
        catch (InterruptedException ignored) {}

        // Stop network - release nodes waiting in receive ...
        network.stop();

        // Tell all nodes to stop and wait for the threads to terminate
        nodes.values().forEach(Node::stop);
    }

    private final int n_nodes;
    private final Network network;
    private final HashMap<Integer, Node> nodes;

    Logger logger = LogManager.getRootLogger();
}
