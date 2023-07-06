package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.nodes.PassAlongRandomNode;
import dev.oxoo2a.sim4da.nodes.TerminatorControlVectorNode;
import dev.oxoo2a.sim4da.nodes.TerminatorDoubleCountingNode;
import dev.oxoo2a.sim4da.times.LamportTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test PassAlongRandomNode with TerminatorControlVectorNode and TerminatorDoubleCountingNode
 * @author Tessa Steinigke
 */
public class TerminationTest {

    private static final int n_nodes = 10;
    private static final int p_prob = 1;

    /***
     * Tests the termination
     */
    @Test
    public void termination() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes+2);
        // Init nodes
        for (int id=0; id<n_nodes; id++) {
            PassAlongRandomNode n = new PassAlongRandomNode(id, new LamportTime(), n_nodes, p_prob);
            s.attachNode(id,n);
        }
        // Terminator watchers
        TerminatorDoubleCountingNode w_dd = new TerminatorDoubleCountingNode(n_nodes, n_nodes);
        s.attachNode(w_dd.myId, w_dd);
        TerminatorControlVectorNode w_cv = new TerminatorControlVectorNode(n_nodes + 1, n_nodes);
        s.attachNode(w_cv.myId, w_cv);
        // Start
        try {
            s.runSimulation(2);
        }
        catch (InstantiationException ignored) {
            fail("Not all nodes instantiated");
        }

        if(!w_dd.hasTermination_detected())
            fail("Double counting methode did not detect termination");
        if(!w_cv.hasTermination_detected())
            fail("Following vector methode did not detect termination");
    }

}
