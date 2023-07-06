package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.TokenRingNode;

import java.util.Optional;
import java.util.Random;
import java.util.StringTokenizer;

public class BaseAktorNode extends Node
{
    private int numberOfNodes;
    private double probability;
    private Random rand;

    private TerminationType terminationType;

    public BaseAktorNode(int my_id, int numberOfNodes, double probability, TerminationType terminationType) {
        super(my_id);
        this.numberOfNodes= numberOfNodes;
        this.probability=probability;
        this.rand= new Random(111+my_id);
        this.messagesReceived=0;
        this.messagesSend=0;
        this.terminationType= terminationType;
    }

    @Override
    protected void main() {

        switch(terminationType){
            case countProcedure -> beAnActorForCountProcedure();
            case vector -> beAnActorForVector();
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


    private int messagesSend;
    private int messagesReceived;

    public void beAnActorForCountProcedure(){
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
    private int [] localVector;

    public void beAnActorForVector(){
        localVector= new int[numberOfNodes];

        Message m = new Message();
        m.getMap().put(MessageNameHelper.baseMessage, "true");
        m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
        int sendTo= getIDExceptSelf();
        onSend(sendTo);
        sendUnicast(sendTo, m);

        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) break;
            m = Message.fromJson(m_raw.payload);

            if(m.getMap().containsKey(MessageNameHelper.baseMessage)){
                onReceive();
                    if (rand.nextDouble() < probability) {
                        m= new Message();
                        m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
                        m.getMap().put(MessageNameHelper.baseMessage, "true");
                        sendTo= getIDExceptSelf();
                        onSend(sendTo);
                        sendUnicast(sendTo, m);
                    } else {
                        System.out.println("Node " + myId + " will not send a message");
                    }


            }else if(m.getMap().containsKey(MessageNameHelper.specialRequest)){

                    String controlVector= m.getMap().get(MessageNameHelper.controlVector);
                    String IDofTerminator= m.getMap().get(MessageNameHelper.ID);
                    String newControlVector= buildControlVectorString(controlVector);
                    m= new Message();
                    m.getMap().put(MessageNameHelper.ID, IDofTerminator);
                    m.getMap().put(MessageNameHelper.controlVector, newControlVector);
                    if(myId == numberOfNodes-1){
                        System.out.println("As im the last node i return controlvector to controller");
                        m.getMap().put(MessageNameHelper.specialAnswer, "true");
                        sendUnicast(Integer.parseInt(IDofTerminator), m);
                    }else {
                        System.out.println("Sending control vector to "+ ((myId + 1) % numberOfNodes));
                        m.getMap().put(MessageNameHelper.specialRequest, "true");
                        sendUnicast((myId + 1) % numberOfNodes, m);
                    }

            }



        }


    }
    private void onReceive(){
        localVector[myId]--;
    }
    private void onSend(int ID){
        localVector[ID]++;
    }
    private int[] calculateControlVector(int [] controlVector){

        if(localVector.length!= controlVector.length)
            throw new IllegalStateException("Vectors have to be the same size");
        for(int i=0; i< localVector.length; i++){

            controlVector[i]+= localVector[i];
        }
        return controlVector;
    }

    private String buildControlVectorString(String controlVectorAsStringFromMessage){

        
        int [] vectorFromMessageAsIntArray= Utils.getVectorFromString(controlVectorAsStringFromMessage, numberOfNodes);
        int [] newControlVector= calculateControlVector(vectorFromMessageAsIntArray);
        String controlVectorAsString="";
        for(int i=0; i< numberOfNodes; i++){
            if(newControlVector[i]<0){
                int value= newControlVector[i]* -1;
                controlVectorAsString+= "-:"+ value+";";
            }else{
                controlVectorAsString+= "+:"+ newControlVector[i]+";";
            }
        }
        return controlVectorAsString;

    }


}
