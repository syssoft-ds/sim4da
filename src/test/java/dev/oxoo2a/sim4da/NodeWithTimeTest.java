package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/***
 * Test nodes with logic clocks
 */
public class NodeWithTimeTest {

    public enum TimeType { LAMPORT, VECTOR }

    private static final int n_nodes = 3;

    /***
     * Same test for both clocks. Just the Time instances are different.
     * @param type - Type of logic clock
     */
    private static void test(TimeType type){
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        NodeWithTime[] nodes = new NodeWithTime[n_nodes];
        for (int id=0; id<n_nodes; id++) {
            Time time;
            if(type == TimeType.LAMPORT)
                time = new LamportTime();
            else
                time = new VectorTime(id, n_nodes);
            NodeWithTime n = new NodeWithTimeRandom(id, time, n_nodes);
            s.attachNode(id,n);
            nodes[id] = n;
        }
        try {
            s.runSimulation(3);
            nodes[0].performEvent(new NodeEvent(NodeEvent.EventType.INNER, -1));
        }
        catch (InstantiationException ignored) {
            fail("Not all nodes instantiated");
        }
        NodeEvent.printAllEvents();
    }

    @Test
    public void testLamportTime(){
        test(TimeType.LAMPORT);
    }

    @Test
    public void testVectorTime() {
        test(TimeType.VECTOR);
    }

}
