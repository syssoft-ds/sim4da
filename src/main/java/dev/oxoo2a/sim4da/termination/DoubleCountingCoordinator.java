package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.ArrayList;
import java.util.List;

public class DoubleCountingCoordinator extends Node {
    List<Integer> receivedValues;
    List<Integer> sentValues;

    int messagesSent1, messagesReceived1, messagesSent2, messagesReceived2;
    int promptsSent;
    int promptsReceived;
    boolean secondPrompt;

    public DoubleCountingCoordinator(int my_id) {
        super(my_id);
        messagesSent1=0;
        messagesReceived1=0;
        messagesReceived2 = 0;
        messagesReceived2 = 0;
        this.receivedValues = new ArrayList<>();
        this.sentValues = new ArrayList<>();
        secondPrompt = false;
    }

    @Override
    protected void main() {

        Message m;
        while(true){
            if(promptsSent<Main.n_nodes){ // TODO: No.
                for (int i = 0; i < Main.n_nodes; i++) {
                    System.out.println("coordinator sending message to " + i);
                    m = new Message();
                    m.add("activation", "double_counting");
                    sendUnicast(i,m);
                    promptsSent++;
                }

            }
            Network.Message m_raw = receive();
            if(m_raw == null){
                System.out.println("Coordinator got null message");
            }else{

                System.out.println("Coordinator received answer");
                m = Message.fromJson(m_raw.payload);
                String sent = m.query("sent");
                String received = m.query("received");
                promptsReceived++;
                // TODO: Gute Moeglichkei für abgleich auasdenken
                if(!secondPrompt){
                    messagesSent1 += Integer.parseInt(sent);
                    messagesReceived1 += Integer.parseInt(received);
                }else{
                    messagesSent2 += Integer.parseInt(sent);
                    messagesReceived2 += Integer.parseInt(received);
                }
                if(promptsSent == Main.n_nodes && promptsReceived == Main.n_nodes){
                    if(secondPrompt){
                        if (messagesSent1 == messagesSent2 && messagesReceived1 == messagesReceived2 && messagesSent1 == messagesReceived1){
                            System.out.println("SYSTEM TERMINATED");
                        }
                        messagesSent1 = 0;
                        messagesReceived1 = 0;
                        messagesSent2=0;
                        messagesReceived2=0;

                        secondPrompt = false;
                    }else{

                        secondPrompt = true;
                    }
                }
            }


        }

    }
}
