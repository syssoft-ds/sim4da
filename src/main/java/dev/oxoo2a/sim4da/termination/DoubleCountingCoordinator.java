package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

public class DoubleCountingCoordinator extends Node {
    int messagesSent, messagesReceived;



    public DoubleCountingCoordinator(int my_id) {
        super(my_id);
        messagesSent=0;
        messagesReceived=0;
    }

    @Override
    protected void main() {

        Message m;
        while(true){
            if(messagesSent ==0){ // TODO: No.
                for (int i = 0; i < Main.n_nodes; i++) {
                    messagesSent++;
                    System.out.println("coordinator sending message to " + i);
                    m = new Message();
                    m.add("activation", "double_counting");
                    sendUnicast(i,m);
                }
            }
            Network.Message m_raw = receive();
            if(m_raw == null){
                System.out.println("Coordinator got null message");
            }else{
                m = Message.fromJson(m_raw.payload);
                String sent = m.query("sent");
                String received = m.query("received");
                // TODO: Gute Moeglichkei für abgleich auasdenken
                messagesSent += Integer.parseInt(sent);
                messagesReceived += Integer.parseInt(received);
            }


        }

    }
}
