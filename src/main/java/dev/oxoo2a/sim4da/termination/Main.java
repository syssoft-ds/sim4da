package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Simulator;

public class Main {
    static int n_nodes = 15;
    static double probability =0.9;
    static int double_count_coordinator_id = n_nodes;
    static int control_vector_coordinator_id = double_count_coordinator_id+1;

    public static void main(String[] args) {
        Node n;
        Simulator s = Simulator.createDefaultSimulator(n_nodes+2);

        for (int id=0; id<n_nodes; id++) {
            n = new ProbabilisticNode(id);
            s.attachNode(id,n);
        }

        n = new DoubleCountingCoordinator(double_count_coordinator_id);
        s.attachNode(double_count_coordinator_id, n);

        n=new ControlVectorCoordinator(control_vector_coordinator_id);
        s.attachNode(control_vector_coordinator_id, n);

        try{
            s.runSimulation(100);
        }catch (InstantiationException e){
            e.printStackTrace();
            System.err.println("Instantiation failed. Time to investigate.");
        }
    }
}