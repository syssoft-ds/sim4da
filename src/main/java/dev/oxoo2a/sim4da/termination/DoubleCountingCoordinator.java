package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

public class DoubleCountingCoordinator extends Node {

    int messagesSent1 = 0;
    int messagesReceived1=0;
    int messagesSent2=0;
    int messagesReceived2=0;
    int promptsReceived;
    boolean secondPrompt=false;
    boolean prompt= true;

    public DoubleCountingCoordinator(int my_id) {
        super(my_id);
    }

    @Override
    protected void main() {
        Message m;

        while(true){
            if(prompt){
                for (int i = 0; i < TerminationMain.n_nodes; i++) {
                    m = new Message();
                    m.add("type", "double_counting");
                    sendUnicast(i,m);
                }
                prompt = false;
            }

            Network.Message m_raw = receive();
            if(m_raw == null){
                System.out.println("Coordinator got null message");
            }else{
                promptsReceived++;
                m = Message.fromJson(m_raw.payload);
                String sent = m.query("sent");
                String received = m.query("received");

                // TODO: Gute Moeglichkei für abgleich auasdenken
                if(!secondPrompt){
                    messagesSent1 += Integer.parseInt(sent);
                    messagesReceived1 += Integer.parseInt(received);
                }else{
                    messagesSent2 += Integer.parseInt(sent);
                    messagesReceived2 += Integer.parseInt(received);
                }
                if(promptsReceived == TerminationMain.n_nodes){
                    promptsReceived = 0;
                    if(secondPrompt){
                        System.out.println("SECOND ROUND IS OVER: VALUES \n"
                                + messagesSent1 + "\n"
                                + messagesReceived1 + "\n"
                                + messagesSent2 + "\n"
                                + messagesReceived2);
                        if (messagesSent1 == messagesSent2
                                && messagesReceived1 == messagesReceived2
                                && messagesSent1 == messagesReceived1){
                            System.out.println("DOUBLE COUNTING SAYS SYSTEM TERMINATED");
                            //System.exit(0);
                            break;
                        }

                        messagesSent1=0;
                        messagesReceived1=0;
                        messagesSent2=0;
                        messagesReceived2=0;

                        secondPrompt = false;
                    }else{
                        secondPrompt = true;
                    }
                    prompt=true;
                }
            }
        }
    }
}
