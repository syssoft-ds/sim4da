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

        /**
         * die folgenden vier werte können beliebig variiert werden
         * ist terminierungstyp der kontrollvektor, kann jedoch nur eine Terminierungsnode vorhanden sein
         */
        int n_terminator = 2;
        int n_nodes = 150;
        double probability= 0.99;
        TerminationType terminationType= TerminationType.countProcedure;

        //wenn der kontrollvektor beim durchlaufen den lokalen Vektor auf einen Nullvektor setzt, darf es die TemrinierungsNode ja nur einmal geben,
        //sonst werden sich die informationen gestohlen -> SPOF oder falsch verstanden
        //in vorheriger Lösung werden lokale Vektor nicht auf Nullvektor gesetzt und immer ein neuer Kotnrolvektor(Nullvektor) losgeschickt, wenn der alter zurückgekommen ist
        //so können leich mehrere TerminierungsNodes gefahren werden
        //In dieser Lösung geht das nicht

        if(terminationType== TerminationType.vector){
            n_terminator=1;
        }
        int allNodes= n_nodes+ n_terminator;
        //ClockTypeToUse clockTypeToUse = ClockTypeToUse.vectorClock;

        Simulator s = Simulator.createDefaultSimulator(allNodes);

        for (int id=0; id<n_nodes; id++) {
            Node n = new BaseAktorNode(id, n_nodes, probability, terminationType);
            s.attachNode(id,n);
        }
        for (int id=n_nodes; id<allNodes; id++) {
            Node n = new TerminationNode(id, n_nodes, 50, terminationType);
            s.attachNode(id,n);
        }
/*
         for (int id=0; id<n_nodes; id++) {
            Node n = new TokenRingNode(id);
            s.attachNode(id,n);
        }

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