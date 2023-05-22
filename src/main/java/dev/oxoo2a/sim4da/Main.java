package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.logicalClocks.LamportClockNode;
import dev.oxoo2a.sim4da.logicalClocks.VectorClockNode;

public class Main {

    public static void main(String[] args) {


        int n_nodes = 5;

        ClockTypeToUse clockTypeToUse = ClockTypeToUse.lamportClock;

        Simulator s = Simulator.createDefaultSimulator(n_nodes);
        /*for (int id=0; id<n_nodes; id++) {
            Node n = new TokenRingNode(id);
            s.attachNode(id,n);
        }*/

        switch (clockTypeToUse){
            case lamportClock -> {
                for (int id=0; id<n_nodes; id++) {
                    Node n = new LamportClockNode(id);
                    s.attachNode(id,n);
                }
            }
            case vectorClock -> {
                for (int id=0; id<n_nodes; id++) {
                    Node n = new VectorClockNode(id,n_nodes);
                    s.attachNode(id,n);
                }
            }
        }



        try{
            s.runSimulation(5);
        }catch (InstantiationException e){
            System.err.println("Instantiation failed. Time to investigate.");
        }
    }
    private enum ClockTypeToUse{
        lamportClock,
        vectorClock
    }
}