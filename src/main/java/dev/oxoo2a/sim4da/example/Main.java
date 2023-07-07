package dev.oxoo2a.sim4da.example;

import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Simulator;
import dev.oxoo2a.sim4da.clock.ClockType;

public class Main {
    public static void main(String[] args) {
        int n_nodes = 5;
        Simulator s = Simulator.createDefaultSimulator(n_nodes);

        for (int id=0; id<n_nodes; id++) {
            //TimedTokenRingNode extends TimedNode -> Constructor expects ClockType
            Node n = new TimedTokenRingNode(id, ClockType.VECTOR);
            s.attachNode(id,n);
        }

        try{
            s.runSimulation(1);
        }catch (InstantiationException e){
            System.err.println("Instantiation failed. Time to investigate.");
        }

    }
}
