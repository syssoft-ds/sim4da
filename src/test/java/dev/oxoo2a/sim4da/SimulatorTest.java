package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimulatorTest {
    
    private static final int numberOfNodes = 3;
    private static final int duration = 2;
    
    @Test
    public void simpleSimulation() {
        Assertions.assertTrue(numberOfNodes == 3);
        Simulator s = Simulator.createDefaultSimulator(numberOfNodes);
        for (int id = 0; id<numberOfNodes; id++) {
            Node n = new BroadcastNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
    
    @Test
    public void someNodesNotInstantiated () {
        Simulator s = Simulator.createDefaultSimulator(numberOfNodes);
        s.attachNode(new BroadcastNode(s, 0));
        s.attachNode(new BroadcastNode(s, 1));
        Assertions.assertThrows(InstantiationException.class,() -> s.runSimulation(duration));
    }
}
