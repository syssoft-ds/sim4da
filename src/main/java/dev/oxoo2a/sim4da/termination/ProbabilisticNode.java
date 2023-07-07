package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.*;

/**
 * Basisaktor: Da diese Nodes sowhol mit beiden Coordinatoren interagieren speichern sie jeweils sowohl die Anzahl
 * gesendeter/empfangener Nachrichten als auch ihren lokalen Vektor V.
 */
public class ProbabilisticNode extends Node {
    private boolean active;
    private int messagesSent;
    private int messagesReceived;
    private int[] V; // Kann ein int-array sein, solange das Indexing stimmt. Deshalb Basisnodes vor Coordinators initialisieren.

    public ProbabilisticNode(int my_id) {
        super(my_id);
        active = true;
        V = new int[TerminationMain.n_nodes];
    }

    @Override
    protected void main() {
        Message m = new Message();
        int receiver;

        /**
         * Für den Start der zufallsbeeinflussten Nachrichten schicken zunächst alle Aktoren eine Basisnachricht an immer
         * einen zufälligen anderen Basisaktor. Bei jedem Sende/Empfangsereignis von Basisnachrichten werden die entsprechenden
         * Felder aktualisiert.
         */
        m.add("type", "base");
        m.add("counter", 1);
        receiver = generateRandomNumber(TerminationMain.n_nodes, myId);
        sendUnicast(receiver, m);
        V[receiver]++;
        messagesSent++;

        while (true){
            /**
             * Die Schleife wartet nur auf Nachrichten. Entsprechend des "type" einer Nachricht werden unterschiedliche Dinge gemacht.
             * Node wird nicht wieder aktiv und es werden keine Counter verändert.
             */
            Network.Message m_raw = receive();
            if(m_raw == null) {
                System.out.println("breaking");
            }
            m = Message.fromJson(m_raw.payload);
            String type = m.query("type");

            /**
             * Double Counting Nachrichten: Sende eigene Counter für sent/received an Coordinator.
             * Node wird nicht wieder aktiv und es werden keine Counter verändert.
             */
            if(Objects.equals(type, "double_counting")) {
                m = new Message();
                m.add("sent", messagesSent);
                m.add("received", messagesReceived);
                sendUnicast(TerminationMain.double_count_coordinator_id, m);

            /**
             * Control Vector Nachrichten:
             * parseVector(): Lese den Vector aus dem Message content, addiere zu lokalem Vector und erzeuge einen neuen Vectorstring
             * für das Ergebnis. Dieser String wird an die nächste Basisnode gesendet, oder zurück an den Coordinator,
             * falls die betrachtete Instanz die letzte Node (id == TerminationMain.n_nodes-1) ist. Node wird wieder aktiv.
             *
             */
            }else if(Objects.equals(type, "control_vector")){
                String vector = m.query("vector");
                // Parse Vector String, add with local vector V and create new Vector String
                String newVectorString = parseVector(vector);

                m = new Message();
                m.add("type", "control_vector");
                m.add("vector", newVectorString);
                //Send message in round trip as in TokenRingNode.java, except the last Node sends back to coordinator
                //This architecture only works if all base nodes are initialized before coordinators.
                receiver = myId== TerminationMain.n_nodes-1 ? TerminationMain.control_vector_coordinator_id : myId+1;
                sendUnicast(receiver,m);
            /**
             * Falls diese Condition greift handelt es sich um eine Basisnachricht. Die Counter werden aktualisiert,
             * die Node wird aktiv und es wird mit der festgelegten Wahrscheinlichkeit eine neue Nachricht an eine zufällig ausgewählte
             * Basisnode geschickt.
             */
            }else{
                int counter = Integer.parseInt(m.query("counter"));
                if(Objects.equals(type, "base")){
                    //decrease own entries
                    V[myId]--;
                    messagesReceived++;
                    active = true;
                }
                counter++;
                m.add("counter", counter);
                //Receiver zufällig bestimmen
                receiver = generateRandomNumber(TerminationMain.n_nodes, myId);
                if(active){
                    Random rand = new Random();
                    if(rand.nextDouble()< TerminationMain.probability){
                        //increase entries
                        messagesSent++;
                        V[receiver]++;
                        sendUnicast(receiver,m);
                    }else{
                        System.out.println(myId + " missed probability");
                    }
                    active = false;
                }
            }
        }
    }

    /**
     * Erzeuge eine Zufallszahl zwischen 0 und range, ohne exclude. Somit senden Nodes ihre Basisnachrichten an andere Basisnodes,
     * aber nicht nochmal an sich selbst.
     */
    public static int generateRandomNumber(int range, int exclude) {
        Random rand = new Random();
        int randomNum;
        do {
            randomNum = rand.nextInt(range);
        } while (randomNum == exclude);
        return randomNum;
    }

    /**
     *
     * @param vectorString Vector der Form "id1:val1;id2:val2;..:"
     * @return neuer Vector (Summe des übergebenen und des lokalen vektors) als String in der entsprechenden Form.
     */
    private String parseVector(String vectorString){
        StringTokenizer tokenizer = new StringTokenizer(vectorString, ";");
        int[] newV = new int[TerminationMain.n_nodes];
        while(tokenizer.hasMoreTokens()){
            String vectorField = tokenizer.nextToken();
            StringTokenizer subTokenizer = new StringTokenizer(vectorField, ":");
            int id = Integer.parseInt(subTokenizer.nextToken());
            int val = Integer.parseInt(subTokenizer.nextToken());

            newV[id] = V[id] + val;
        }
        String newVectorString ="";
        for (int i = 0; i < newV.length-1; i++) {
            newVectorString = newVectorString + i +":" + newV[i] + ";";
        }
        newVectorString = newVectorString + (newV.length-1)  +":" + newV[newV.length-1];
        return newVectorString;
    }
}
