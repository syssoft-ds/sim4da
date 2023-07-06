package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.*;

public class ProbabilisticNode extends Node {
    private boolean active;
    private int messagesSent;
    private int messagesReceived;
    private Map<Integer, Integer> localVector;
    private int[] V;

    public ProbabilisticNode(int my_id) {
        super(my_id);
        active = true;
        V = new int[Main.n_nodes];
    }

    @Override
    protected void main() {
        Message m = new Message();
        int receiver;

        m.add("type", "base");
        m.add("counter", 1);
        receiver = generateRandomNumber(Main.n_nodes, myId);
        sendUnicast(receiver, m);
        V[receiver]++;
        messagesSent++;

        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) {
                System.out.println("breaking");
            }
            m = Message.fromJson(m_raw.payload);
            String type = m.query("type");

            if(Objects.equals(type, "double_counting")) {
                m = new Message();
                m.add("sent", messagesSent);
                m.add("received", messagesReceived);
                sendUnicast(Main.double_count_coordinator_id, m);

            }else if(Objects.equals(type, "control_vector")){
                String vector = m.query("vector");
                for (int i = 0; i < V.length; i++) {
                    System.out.print(i + ":" + V[i] + ";");
                }
                // Parse Vector String, add with local vector V and create new Vector String
                String newVectorString = parseVector(vector);

                m = new Message();
                m.add("type", "control_vector");
                m.add("vector", newVectorString);
                //Send message in round trip as in TokenRingNode.java, except the last Node sends back to coordinator
                //This architecture only works if all base nodes are initialized before coordinators.
                receiver = myId==Main.n_nodes-1 ? Main.control_vector_coordinator_id : myId+1;
                sendUnicast(receiver,m);
            }else{
                int counter = Integer.parseInt(m.query("counter"));
                if(Objects.equals(type, "base")){
                    //decrease own vector entry
                    V[myId]--;
                    messagesReceived++;
                    active = true;
                }
                //emit("%d: activation == %d", myId, counter);
                counter++;
                m.add("counter", counter);
                receiver = generateRandomNumber(Main.n_nodes, myId);
                if(active){
                    Random rand = new Random();
                    if(rand.nextDouble()<Main.probability){
                        messagesSent++;
                        //increase receiver vector entry
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

    public static int generateRandomNumber(int range, int exclude) {
        Random rand = new Random();
        int randomNum;
        do {
            randomNum = rand.nextInt(range);
        } while (randomNum == exclude);
        return randomNum;
    }

    private String parseVector(String vectorString){
        StringTokenizer tokenizer = new StringTokenizer(vectorString, ";");
        int[] newV = new int[Main.n_nodes];
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
