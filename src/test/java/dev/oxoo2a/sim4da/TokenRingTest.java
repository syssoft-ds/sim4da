package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Simulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class TokenRingTest {

    private static final int n_nodes = 10;

    /***
     * Tests the token Ring
     */
    @Test
    public void tokenRingTest() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            TokenRingNode trn = new TokenRingNode(id);
            s.attachNode(id,trn);
        }
        try {
            s.runSimulation(2);
        }
        catch (InstantiationException ignored) {
            fail("Not all nodes instantiated");
        }
    }

}
