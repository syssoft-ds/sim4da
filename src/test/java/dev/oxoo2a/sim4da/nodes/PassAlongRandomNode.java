package dev.oxoo2a.sim4da.nodes;

import dev.oxoo2a.sim4da.*;

import java.util.Arrays;
import java.util.Random;

/***
 * Jeder Aktor beginnt mit einer aktiven Phase, in der er mit einer parametrisierten
 * Wahrscheinlichkeit p eine Nachricht an einen zufällig ausgewählten anderen Aktor sendet. Anschließend wechselt der sendende
 * Aktor in den passiven Zustand. Ein passiver Aktor kann durch den Empfang einer Nachricht wieder aktiv werden. In diesem Fall
 * sendet er erneut mit der gegebenen Wahrscheinlichkeit p eine Nachricht an einen neu ausgewählten zufälligen Aktor. Die
 * Wahrscheinlichkeit p ist für alle n Aktoren gleich und soll über den zeitlichen Verlauf der Simulation gegen 0 konvergieren.
 * @author Tessa Steinigke
 */
public class PassAlongRandomNode extends NodeWithTime {

    private final int n_nodes;
    // 100% = 1; 50% = 2; 25% = 4; ...
    private int p_prob;
    // converges to 0
    private static final int p_increase_step = 1;

    private boolean status_active = true;
    private int c_send = 0;
    private int c_received = 0;
    private int[] v_messages;

    /***
     *
     * @param my_id - node id
     * @param time - node time instance
     * @param n_nodes - number of nodes that are communicating (without watchers)
     * @param p_prob - starting probability to pass along a received message (1 = 100%, 2 = 50%, ...)
     */
    public PassAlongRandomNode(int my_id, Time time, int n_nodes, int p_prob) {
        super(my_id, time);
        this.n_nodes = n_nodes;
        this.p_prob = p_prob;
        v_messages = new int[n_nodes];
    }

    /**
     * Send a new message to a random receiver by the given probability
     */
    private void send_pass_along(){
        Random rand = new Random();
        // Probability to send message
        int p = rand.nextInt(p_prob);
        if(p == 0){
            // Send
            int rand_receiver = rand.nextInt(n_nodes);
            sendUnicast(rand_receiver, new Message());
            c_send++;
            v_messages[rand_receiver] += 1;
            p_prob += p_increase_step;
            if(p_prob < 0) p_prob = 0;
            status_active = false;
        }else
            emit("End of one message passing: %d", myId);
    }

    /**
     * Send the requested status to a watcher
     * @param request - request type
     * @param requester_id - watcher node id
     */
    private void send_my_status(String request, int requester_id){
        if(request.equals("send/received")){
            // Status values requested
            Message m_status = new Message();
            m_status.add("send", c_send);
            m_status.add("received", c_received);
            m_status.add("status", (status_active) ? "active" : "passive");
            sendUnicast(requester_id, m_status);
        }else if(request.equals("vector")){
            Message m_status = new Message();
            m_status.add("vector", Arrays.toString(v_messages));
            v_messages = new int[n_nodes];
            sendUnicast(requester_id, m_status);
        }
    }

    @Override
    protected void main() {
        Message m;
        send_pass_along();
        while (true) {
            // Listen for messages
            Network.Message m_raw = receive();
            if (m_raw == null) break;

            // Message received
            m = Message.fromJson(m_raw.payload);
            String request = m.query("request");
            if(request == null) {
                // Basic message
                if(status_active) break;
                c_received++;
                v_messages[myId] -= 1;
                send_pass_along();
            }else
                // Special requests
                send_my_status(request, m_raw.sender_id);
        }
    }
}
