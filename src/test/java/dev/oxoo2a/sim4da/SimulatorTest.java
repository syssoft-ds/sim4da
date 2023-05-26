package dev.oxoo2a.sim4da;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

public class SimulatorTest {
    private final int n_nodes = 3;
    private final int duration = 10;

    @Test
    public void simpleSimulation() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            Node n = new BroadcastNode(id);
            s.attachNode(id,n);
        }
        try {
            s.runSimulation(duration, "lamport");
        }
        catch (Exception ignored) {
            fail("Not all nodes instantiated");
        }

    }

    @Test
    public void someNodesNotInstantiated () {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        s.attachNode(0,new BroadcastNode(0));
        s.attachNode(1,new BroadcastNode(1));
        assertThrows(InstantiationException.class,() -> {s.runSimulation(duration, "lamport");});
    }
}
