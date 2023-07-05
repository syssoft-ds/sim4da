package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Simulator;

public class Main {
    static int n_nodes = 5;
    static double probability =0.9;
    static int double_count_coordinator_id = -1;

    public static void main(String[] args) {

        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        for (int id=0; id<n_nodes; id++) {
            Node n = new ProbabilisticNode(id);
            s.attachNode(id,n);
        }
        Node n = new DoubleCountingCoordinator(double_count_coordinator_id);
        s.attachNode(double_count_coordinator_id, n);

        try{
            s.runSimulation(100);
        }catch (InstantiationException e){
            System.err.println("Instantiation failed. Time to investigate.");
        }
    }
}