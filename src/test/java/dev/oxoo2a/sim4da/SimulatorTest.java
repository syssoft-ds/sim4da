package dev.oxoo2a.sim4da;

import static org.junit.jupiter.api.Assertions.*;
import dev.oxoo2a.sim4da.LamportClock;
import dev.oxoo2a.sim4da.VectorClock;


import java.beans.Transient;

import org.junit.jupiter.api.Test;

public class SimulatorTest {
    private final int n_nodes = 3;
    private final int duration = 2;

    @Test
    public void simpleSimulation() {
        //Simulator s = Simulator.createDefaultSimulator(n_nodes, new LamportClock();
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        Tracer tracer = new Tracer("TestTracer", true, true, true, System.out);
        Network network = new Network(n_nodes, tracer, s.getClock());
        network.setClock(new VectorClock(n_nodes, 1,0));


        for (int id=0; id<n_nodes; id++) {
            Node n = new BroadcastNode(id, (VectorClock) s.getClock(), tracer);
            s.attachNode(id,n);
        }
        s.setNetwork(network);

        try {
            s.runSimulation(duration);
        }
        catch (InstantiationException ignored) {
            fail("Not all nodes instantiated");
        }
    }


    @Test
    public void someNodesNotInstantiated() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        Tracer tracer = new Tracer("TestTracer", true, true, true, System.out);
        s.attachNode(0, new BroadcastNode(0, (VectorClock) s.getClock(), tracer));
        s.attachNode(1, new BroadcastNode(1, (VectorClock) s.getClock(), tracer));

//        try {
//            s.runSimulation(duration);
//            fail("Expected InstantiationException to be thrown, but nothing was thrown.");
//        } catch (InstantiationException e) {
//            // Expected exception was thrown, the test case passes
//        }

        // Use assertThrows to verify that an InstantiationException is thrown
        assertThrows(InstantiationException.class, () -> {
            s.runSimulation(duration);
        });

    }

}
