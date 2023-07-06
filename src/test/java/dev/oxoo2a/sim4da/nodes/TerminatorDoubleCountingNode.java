package dev.oxoo2a.sim4da.nodes;

import dev.oxoo2a.sim4da.*;

/**
 * Doppelzählverfahren zur Bestimmung der Terminierung
 * @author Tessa Steinigke
 */
public class TerminatorDoubleCountingNode extends Terminator {

    /**
     * [Interval 1: sums of send|received|active],
     * [Interval 2: sums of send|received|active]
     */
    private int[][] statistics;
    private int statistics_interval = 0;
    private int c_statistics_missing = n_nodes;

    public TerminatorDoubleCountingNode(int my_id, int n_nodes) {
        super(my_id, n_nodes);
        statistics = new int[2][3];
    }

    protected void send_request(){
        c_statistics_missing = n_nodes;
        Message m =  new Message();
        m.add("request","send/received");
        sendBroadcast(m);
    }

    protected void receive_status(Message m){
        if(m.query("request")!= null) return; // Ignore requests

        c_statistics_missing--;
        // Add statistics
        statistics[statistics_interval][0] += Integer.parseInt(m.query("send"));
        statistics[statistics_interval][1] += Integer.parseInt(m.query("received"));
        statistics[statistics_interval][2] += (m.query("status").equals("active") ? 1 : 0 );

        // Analyse
        if(c_statistics_missing == 0){
            // All answers received
            if(statistics_interval == 0) {
                // first loop
                statistics_interval = 1;
                send_request();
                return;
            }

            //emit("0 missing: (%d,%d,%d),(%d,%d,%d)", stati[0][0], stati[0][1], stati[0][2], stati[1][0], stati[1][1], stati[1][2]);

            // Check terminated
            // 1. check all passive
            boolean terminated = (statistics[0][2] + statistics[1][2] == 0);
            if(terminated){
                //2. Check values
                int val = statistics[0][0];
                terminated = (val == statistics[0][1]) & (val == statistics[1][0]) & (val == statistics[1][1]);
                if(terminated){
                    // TERMINATED
                    termination_detected = true;
                    emit("!! TERMINATION DETECTED BY DOUBLE-COUNTING-METHODE");
                    return;
                }
            }

            // Not terminated
            if(statistics[1][0] == statistics[1][1]){
                // Last attempt could indicate termination
                statistics[0] = statistics[1]; // shift last to new first
                statistics[1] = new int[3]; // empty new last
            }else{
                // Last attempt already indicates no termination -> can be deleted
                statistics = new int[2][3];
                statistics_interval = 0;
            }
            send_request();
        }
    }

}
