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
        localVector = new HashMap<>();
        for (int i = 0; i < Main.n_nodes; i++) {
            localVector.put(i, 0);
        }
        V = new int[Main.n_nodes];
    }

    @Override
    protected void main() {
        Message m = new Message();

        m.add("type", "base");
        m.add("counter", 1);
        sendUnicast(generateRandomNumber(Main.n_nodes, myId), m);
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
                System.out.println("NODE " + myId+ " RECEIVED CONTROL VECTOR MESSAGE");
                String vector = m.query("vector");
                parseVector(vector);
                String newVectorString = buildVectorString();
                System.out.println("NEW Vector String: " + newVectorString);
                //TODO: Send Message around to each Node (as in TokenRingNode). Finally send back to Coordinator
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
                int receiver = generateRandomNumber(Main.n_nodes, myId);
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

    private void parseVector(String vectorString){
        StringTokenizer tokenizer = new StringTokenizer(vectorString, ";");
        while(tokenizer.hasMoreTokens()){
            String vectorField = tokenizer.nextToken();
            StringTokenizer subTokenizer = new StringTokenizer(vectorField, ":");
            int id = Integer.parseInt(subTokenizer.nextToken());
            int val = Integer.parseInt(subTokenizer.nextToken());
            V[id] = V[id] + val;
        }
    }

    private String buildVectorString(){
        String s ="";
        for (int i = 0; i < V.length-1; i++) {
            s = s + i +":" + V[i] + ";";
        }
        s = s + (V.length-1)  +":" + V[V.length-1];
        return s;
    }
}
