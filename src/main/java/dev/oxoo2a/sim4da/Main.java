package dev.oxoo2a.sim4da;

public class Main {
    private static final int n_nodes =10;

    public static void main(String[] args) throws InstantiationException {
        Clock clock = new LamportClock(); //set Logical Clock type
        Tracer tracer = new Tracer("Main", true, true, true, System.out);
        Network network = new Network(n_nodes, tracer, clock);
        Simulator s = Simulator.createDefaultSimulator(n_nodes, clock);

        for (int id=0; id<n_nodes; id++) {
            TokenRingNode trn = new TokenRingNode(id, clock, tracer);
            s.attachNode(id,trn);
        }
        s.runSimulation(2);
    }

}
