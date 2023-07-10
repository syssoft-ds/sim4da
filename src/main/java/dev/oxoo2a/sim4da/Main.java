package dev.oxoo2a.sim4da;

public class Main {

    public static void main(String[] args) {
        int n_nodes = 5;
        int duration = 26;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        int type = 0; // 0 = Counter Actor, 1 = Vector Actor

        for (int id=0; id<n_nodes; id++) {
            /*
            // u1, sending messages and tracking time withLamport or Vertex Clock
            TokenRingNode n = new TokenRingNode(id, new LamportClock());
            */

            if (type == 0) {
                if (id == 0) {
                    // u2  Double Count Actors that detects termination
                    DoubleCountActor first = new DoubleCountActor(id);
                    s.attachNode(id, first);
                } else {
                    // u2 sending messages with Actors and detect termination
                    BaseActor n = new CountActor(id);
                    s.attachNode(id, n);
                }
            }else{
                if (id == 0) {
                    // u2  Vector Controll Actor that detects termination
                    VectorControlActor first = new VectorControlActor(id, n_nodes);
                    s.attachNode(id, first);
                } else {
                    // u2 sending messages with Actors and detect termination
                    VectorActor n = new VectorActor(id, n_nodes);
                    s.attachNode(id, n);
                }
            }
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
