package dev.oxoo2a.sim4da;

public class Main {

    public static void main(String[] args) {
        int n_nodes = 5;
        int duration = 16;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        for (int id=0; id<n_nodes; id++) {
            /*
            // u1, sending messages and tracking time withLamport or Vertex Clock
            TokenRingNode n = new TokenRingNode(id, new LamportClock());
            */

            if (id == 0) {
                // u2  Double Count Actors that detects termination
                DoubleCountActor first = new DoubleCountActor(id);
                s.attachNode(id, first);
            } else {
                // u2 sending messages with Actors and detect termination
                Actor n = new Actor(id);
                s.attachNode(id, n);
            }
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
