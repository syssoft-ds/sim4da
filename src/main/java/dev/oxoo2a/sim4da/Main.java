package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.logicalClocks.LamportClockNode;
import dev.oxoo2a.sim4da.logicalClocks.VectorClockNode;
import dev.oxoo2a.sim4da.termination.BaseAktorNode;
import dev.oxoo2a.sim4da.termination.TerminationNode;
import dev.oxoo2a.sim4da.termination.TerminationType;
import dev.oxoo2a.sim4da.termination.TimeManager;

public class Main {

    public static void main(String[] args) {

        TimeManager.setTimer();

        int n_terminator = 1;
        int n_nodes = 150;
        int allNodes= n_nodes+ n_terminator;
        TerminationType terminationType= TerminationType.vector;
        ClockTypeToUse clockTypeToUse = ClockTypeToUse.vectorClock;

        Simulator s = Simulator.createDefaultSimulator(allNodes);
        /*for (int id=0; id<n_nodes; id++) {
            Node n = new TokenRingNode(id);
            s.attachNode(id,n);
        }*/
        for (int id=0; id<n_nodes; id++) {
            Node n = new BaseAktorNode(id, n_nodes, 0.99, terminationType);
            s.attachNode(id,n);
        }
        for (int id=n_nodes; id<allNodes; id++) {
            Node n = new TerminationNode(id, n_nodes, 1000, terminationType);
            s.attachNode(id,n);
        }
/*
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
        */




        try{
            s.runSimulation(Integer.MAX_VALUE);
        }catch (InstantiationException e){
            System.err.println("Instantiation failed. Time to investigate.");
        }
    }
    private enum ClockTypeToUse{
        lamportClock,
        vectorClock
    }
}