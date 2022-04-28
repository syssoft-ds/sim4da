package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Test;
public class SimulatorTest {

    @Test
    public static void simpleSimulation() {
        int n_nodes = 5;
        int duration = 20;

        Simulator s = new Simulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            Node n = new BroadcastNode(id);
            s.attachNode(id,n);
        }
        try {
            s.runSimulation(duration);
        }
        catch (InstantiationException ignored) {}
    }
}
