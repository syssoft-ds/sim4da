package dev.oxoo2a.sim4da.nodes;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

public abstract class Terminator extends Node {

    protected final int n_nodes;

    protected boolean termination_detected = false;

    public Terminator(int my_id, int n_nodes) {
        super(my_id);
        this.n_nodes = n_nodes;
    }

    protected abstract void send_request();

    protected abstract void receive_status(Message m);

    public boolean hasTermination_detected(){
        return termination_detected;
    }

    @Override
    protected void main() {
        send_request();
        while (true) {
            if(termination_detected) break;
            // Listen for messages
            Network.Message m_raw = receive();
            if (m_raw == null) break;
            // Message received
            receive_status(Message.fromJson(m_raw.payload));
        }
    }

}
