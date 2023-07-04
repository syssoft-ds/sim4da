package dev.oxoo2a.sim4da;

public class Main {

    public static void main(String[] args) {
        int n_nodes = 5;
        int duration = 5;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        for (int id=0; id<n_nodes; id++) {
            /*
            // u1, sending messages and tracking time withLamport or Vertex Clock
            TokenRingNode n = new TokenRingNode(id, new LamportClock());
            */

            // u2 sending messages with Actors and detect termination
            Actor n = new Actor(id);
            s.attachNode(id,n);
        }

        try {
            s.runSimulation(duration);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
