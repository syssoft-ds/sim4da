package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

/**
 * Double Counting Coordinator:
 * Speichert Felder mit der Summe empfangener/gesendeter Nachrichten für beide Durchläufe. Zusätzlich zwei booleans 'prompt'
 * um zu kontrollieren, wann ein Doppelzählverfahren angestoßen wird, und seconds prompt um zu unterscheiden um
 * welchen Durchlauf es sich handelt. int promptsReceived zählt mit, wie viele Nachrichten von den Basisaktoren
 * zurückkommen und vergleicht dies mit der Anzahl von Basisaktoren um zu prüfen, wann ein Durchlauf abgeschlossen ist.
 *
 */
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

        /**
         * Schickt eine Aufforderung an alle Basisklassen. Hier ist kein broadcast() verwendet, damit die Nachricht
         * nicht auch an den ControlVectorCoordinator geht. Diese ließe sich aber auch einfach dort abfangen.
         */
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
            /**
             *  Routine für das Empfangen von Nachrichten. Werte auslesen und in die entsprechenden Felder speichern,
             *  je nachdem ob dies der erste oder zweite Zähldurchlauf ist.
             */
            }else{
                promptsReceived++;
                m = Message.fromJson(m_raw.payload);
                String sent = m.query("sent");
                String received = m.query("received");

                if(!secondPrompt){
                    messagesSent1 += Integer.parseInt(sent);
                    messagesReceived1 += Integer.parseInt(received);
                }else{
                    messagesSent2 += Integer.parseInt(sent);
                    messagesReceived2 += Integer.parseInt(received);
                }
                /**
                 * Falls alle Basisaktoren Nachrichten zurückgeschickt haben: Prüfe ob die Werte übereinstimmen
                 * -> Entweder terminieren oder boolean 'prompt' auf true setzten, damit das nächste Doppelzählverfahren startet.
                 */
                if(promptsReceived == TerminationMain.n_nodes){
                    promptsReceived = 0;
                    //nur was machen falls dies der zweite durchlauf war. Ansonsten boolean 'secondPrompt' setzen um
                    // signalisieren, dass der zweite Durchlauf jetzt stattfindet.
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
