package dev.oxoo2a.sim4da;

public class Main {
    private final int n_nodes = 3;
    private final int duration = 10;

    public  void simpleSimulation() {
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            Node n = new SimulationNode(id);
            s.attachNode(id,n);
        }
        try {
            s.runSimulation(duration, "lamport");
        }
        catch (Exception ignored) {

        }

    }
    public static void main(String[] args)
    {
        Main m = new Main();
        m.simpleSimulation();
    }

}
