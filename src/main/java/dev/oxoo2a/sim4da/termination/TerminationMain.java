package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Simulator;

public class TerminationMain {
    /**
     * Die statischen Variablen werden benutzt um aus allen Nodes auf die relevanten größen zuzugreifen. In einem echten
     * verteilen System ginge das so natürlich nicht, aber man könnte diese Informationen zB Initial an alles Aktoren schicken
     * und als Aktor mit dem Beginn der eigentlichen Funktionalität auf Erhalt dieser Initialisierungsnachricht warten.
     * Damit die IDs stimmen ist es in meiner Implementierung wichtig, dass die Basisaktoren zuerst initialisiert werden.
     *
     * Die Simulation läuft für 100 sekunden, sobald die Terminierung festegestellt wurde wird das Programm aber mit
     * System.exit(0) beendet. Dieser Aufruf ist momentan in der ControlVectorCoordinator Klasse, da diese bisher immer
     * ein kleines bisschen langsamer ist.
     * Beide Coordinator melden die Terminierung in der Konsole, sodass die korrekte  Terminierung von beiden geprüft werden kann.
     * Der Print ist "[...] SAYS SYSTEM TERMINATED"
     */
    static int n_nodes = 150;
    static double probability =0.99;
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