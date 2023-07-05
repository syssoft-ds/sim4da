package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.Random;

public class ProbabilisticNode extends Node {
    private boolean active;
    private int messagesSent;
    private int messagesReceived;

    public ProbabilisticNode(int my_id) {
        super(my_id);
        active = true;
    }

    @Override
    protected void main() {
        Message m = new Message();

        m.add("type", 1);
        m.add("counter", 1);
        sendUnicast(generateRandomNumber(Main.n_nodes, myId), m);

        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) {
                System.out.println("breaking");
            }
            System.out.println(m_raw);

            m = Message.fromJson(m_raw.payload);

            int type = Integer.parseInt(m.query("type"));
            int counter = Integer.parseInt(m.query("counter"));
            if(type == 1){
                messagesReceived++;
                active = true;
            }


            //emit("%d: activation == %d", myId, counter);
            counter++;
            m.add("counter", counter);
            int receiver = generateRandomNumber(Main.n_nodes, myId);
            if(active){
                Random rand = new Random();
                messagesSent++;
                if(rand.nextDouble()<Main.probability){
                    sendUnicast(receiver,m);

                }else{
                    System.out.println(myId + " missed probability");
                }

                active = false;
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
}
