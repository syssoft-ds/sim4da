package dev.oxoo2a.sim4da.test;

//import org.junit.jupiter.api.Test;
//import dev.oxoo2a.sim4da.Simulator;
//import dev.oxoo2a.sim4da.Node;

public class SimulatorTest {

    @Test
    public static void simpleSimulation() {
        int n_nodes = 5;
        int duration = 5;

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
