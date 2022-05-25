package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimulatorTest {
    
    private static final int NUMBER_OF_NODES = 3;
    private static final int DURATION = 2;
    
    @Test
    public void simpleSimulation() {
        Assertions.assertEquals(NUMBER_OF_NODES, 3);
        Simulator s = Simulator.createDefaultSimulator(NUMBER_OF_NODES);
        for (int id = 0; id<NUMBER_OF_NODES; id++) {
            Node n = new BroadcastNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(DURATION);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
    
    @Test
    public void someNodesNotInstantiated () {
        Simulator s = Simulator.createDefaultSimulator(NUMBER_OF_NODES);
        s.attachNode(new BroadcastNode(s, 0));
        s.attachNode(new BroadcastNode(s, 1));
        Assertions.assertThrows(InstantiationException.class,() -> s.runSimulation(DURATION));
    }
}
