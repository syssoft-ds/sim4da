package dev.oxoo2a.sim4da;

public class Main {
    public static void main(String[] args) {

        int n_nodes = 5;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        for (int id=0; id<n_nodes; id++) {
            Node n = new TokenRingNode(id);
            s.attachNode(id,n);
        }
        try{

            s.runSimulation(1000000000);
        }catch (InstantiationException e){
            System.err.println("Instantiation failed. Time to investigate.");
        }
    }
}