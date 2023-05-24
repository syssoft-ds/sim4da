package dev.oxoo2a.sim4da;

public class Main {

    public static void main(String[] args) {
        int n_nodes = 10;
        int duration = 5;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        for (int id=0; id<n_nodes; id++) {
            TokenRingNode n = new TokenRingNode(id, Node.ClockType.LAMPORT);
            s.attachNode(id,n);
        }

        try {
            s.runSimulation(duration);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
