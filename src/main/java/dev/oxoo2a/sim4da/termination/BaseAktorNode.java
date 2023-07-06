package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.TokenRingNode;

import java.util.Random;

public class BaseAktorNode extends Node
{
    private int numberOfNodes;
    private double probability;
    private Random rand;
    private int messagesSend;
    private int messagesReceived;

    public BaseAktorNode(int my_id, int numberOfNodes, double probability) {
        super(my_id);
        this.numberOfNodes= numberOfNodes;
        this.probability=probability;
        this.rand= new Random(111+my_id);
        this.messagesReceived=0;
        this.messagesSend=0;
    }

    @Override
    protected void main() {


        Message m = new Message();
        m.getMap().put(MessageNameHelper.baseMessage, "true");
        m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
        sendUnicast(getIDExceptSelf(), m);
        messagesSend++;

        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) break;
            m = Message.fromJson(m_raw.payload);

            if(m.getMap().containsKey(MessageNameHelper.baseMessage)){
                messagesReceived++;
                System.out.println("Incremented received");
                if(rand.nextDouble()< probability){
                    messagesSend++;
                    System.out.println("Incremented send");
                    m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
                    m.getMap().put(MessageNameHelper.baseMessage, "true");
                    sendUnicast(getIDExceptSelf(),m);

                }else{
                    System.out.println("Node "+ myId+" will not send a message");
                }

            }else if(m.getMap().containsKey(MessageNameHelper.specialRequest)){
                String iteration= m.getMap().get(MessageNameHelper.iteration);
                int ID= Integer.parseInt(m.getMap().get(MessageNameHelper.ID));
                m= getSpecialMessage(iteration);
                sendUnicast(ID, m);
            }



        }
    }

    private Message getSpecialMessage(String iteration){
        Message m= new Message();
        m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
        m.getMap().put(MessageNameHelper.iteration, iteration);
        m.getMap().put(MessageNameHelper.specialAnswer, "true");
        m.getMap().put(MessageNameHelper.messagesReceived,getMessagesReceived() );
        m.getMap().put(MessageNameHelper.messagesSend,getMessagesSend());
        return m;
    }



    public String getMessagesSend() {
        return String.valueOf(messagesSend);
    }

    public String getMessagesReceived() {
        return String.valueOf(messagesReceived);
    }

    public int getIDExceptSelf(){
        int id= myId;
        while(id== myId)
            id= rand.nextInt(numberOfNodes);
        return id;
    }
}
