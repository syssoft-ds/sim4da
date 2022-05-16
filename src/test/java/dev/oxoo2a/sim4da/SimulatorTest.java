package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimulatorTest {
    
    private static final int n_nodes = 3;
    private static final int duration = 2;
    
    @Test
    public void simpleSimulation() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        for (int id = 0; id<n_nodes; id++) {
            Node n = new BroadcastNode(id);
            s.attachNode(id, n);
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
    
    @Test
    public void someNodesNotInstantiated () {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        s.attachNode(0, new BroadcastNode(0));
        s.attachNode(1, new BroadcastNode(1));
        Assertions.assertThrows(InstantiationException.class,() -> s.runSimulation(duration));
    }
}
