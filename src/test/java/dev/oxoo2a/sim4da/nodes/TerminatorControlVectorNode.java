package dev.oxoo2a.sim4da.nodes;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

/**
 * Nachlaufender Kontrollvector zur Bestimmung der Terminierung
 * @author Tessa Steinigke
 */
public class TerminatorControlVectorNode extends Terminator {

    private final int[] statistics;
    private int node_request_next_statistics = 0;

    public TerminatorControlVectorNode(int my_id, int n_nodes) {
        super(my_id, n_nodes);
        statistics = new int[n_nodes];
    }

    protected void send_request(){
        Message m =  new Message();
        m.add("request","vector");
        sendUnicast(node_request_next_statistics, m);
        node_request_next_statistics = (node_request_next_statistics +1) % n_nodes;
    }

    protected void receive_status(Message m){
        if(m.query("request")!= null) return; // Ignore requests

        String sender_v_s = m.query("vector");
        sender_v_s = sender_v_s.substring(1, sender_v_s.length()-1);
        String[] sender_v = sender_v_s.split(", ");
        boolean running = false;
        for (int i = 0; i < statistics.length; i++){
            statistics[i] += Integer.parseInt(sender_v[i]);
            if(statistics[i] > 0) running = true;
        }

        // Check termination
        if(!running){
            // TERMINATED
            termination_detected = true;
            emit("!! TERMINATION DETECTED BY FOLLOWING-VECTOR-METHODE");
        }else{
            // Not terminated
            send_request();
        }
    }

}
