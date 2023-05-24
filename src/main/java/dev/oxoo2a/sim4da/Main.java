package dev.oxoo2a.sim4da;

public class Main {
    private static final int n_nodes =10;  //constant. Number of nodes in simulation
    public static void main(String[] args) throws InstantiationException {

        VectorClock clock = new VectorClock(n_nodes, 0, 0); //Initialize object. Will be shared among all nodes

        Simulator s = Simulator.createDefaultSimulator(n_nodes); //passing number of nodes

        //iterating over node ID
        for (int id=0; id<n_nodes; id++) {
            TokenRingNode trn = new TokenRingNode (id, clock,  s.getTracer()); //creating object trn
            s.attachNode(id,trn); //attaching trn to simulator
        }
        s.runSimulation(2);
    }

}
