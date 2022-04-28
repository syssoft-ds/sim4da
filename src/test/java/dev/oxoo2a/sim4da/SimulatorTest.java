package dev.oxoo2a.sim4da;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SimulatorTest {

    @Test
    public void simpleSimulation() {
        int n_nodes = 3;
        int duration = 5;

        Simulator s = new Simulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            Node n = new BroadcastNode(id);
            s.attachNode(id,n);
        }
        try {
            s.runSimulation(duration);
        }
        catch (InstantiationException ignored) {
            fail("Not all nodes instantiated");
        }
    }
}
