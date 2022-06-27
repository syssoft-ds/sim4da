package dev.oxoo2a.sim4da;

import java.util.Random;

import dev.oxoo2a.sim4da.Simulator.TimestampType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LeLannTest {
    
    private static final int NUMBER_OF_NODES = 3;
    private static final int DURATION = 2;
    
    @Test
    public void runLeLannSimulation() {
        Simulator s = new Simulator(NUMBER_OF_NODES, TimestampType.VECTOR, "LeLann", true, System.out, true);
        for (int id = 0; id<NUMBER_OF_NODES; id++) {
            Node n = new LeLannNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(DURATION);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
    
    private static class LeLannNode extends Node {
        private LeLannNode(Simulator s, int id) {
            super(s, id);
        }
        private void doCriticalSectionIfRequired() {
            // do all required critical stuff that requires mutual exclusion here
        }
        @Override
        public void run() {
            emitToTracer("This is node %d", id);
            if (id==0) {
                int nextNode = new Random().nextInt(getNumberOfNodes()); // start token ring at a random node
                emitToTracer("Node %d: Sending initial token to node %d", id, nextNode);
                sendUnicast(nextNode, "TOKEN");
            }
            while (isStillSimulating()) {
                Message m = receive();
                if (m==null) break; // null==simulation time ends while waiting for a message
                emitToTracer("Node %d: Received %s", id, m.toString());
                if (m.getPayload().equals("TOKEN")) {
                    doCriticalSectionIfRequired();
                    // now forward token to next node
                    int nextNode = (id+1) % getNumberOfNodes();
                    sendUnicast(nextNode, "TOKEN");
                }
            }
        }
    }
}
