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
    private final int numberOfNodes;
    private final double probability;
    private final Random rand;

    private final TerminationType terminationType;

    public BaseAktorNode(int my_id, int numberOfNodes, double probability, TerminationType terminationType) {
        super(my_id);
        this.numberOfNodes= numberOfNodes;
        this.probability=probability;
        this.rand= new Random(111+my_id);
        this.terminationType= terminationType;
    }

    @Override
    protected void main() {

        switch(terminationType){
            case countProcedure -> beAnActorForCountProcedure();
            case vector -> beAnActorForVector();
        }

    }



    /**
     * wird dies in der main aufgerufen verhält sich die Node entlang einer terminierung mit Doppelzaehlverfahren
     * erhält die node eine NAchricht bei dem die map einen Schlüssel specialRequest ist diese Nachricht von einer der Kontrollnodes
     * Es wird geantwortet mit der Anzahl an gesendeten und empfangenen nachrichten
     * Ansonsten werden Base Messages mit einer Wahrscheinlichkeit von x versand
     */

    public void beAnActorForCountProcedure(){
        this.messagesReceived=0;
        this.messagesSend=0;
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

                if(rand.nextDouble()< probability){

                    messagesSend++;
                    m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
                    m.getMap().put(MessageNameHelper.baseMessage, "true");
                    sendUnicast(getIDExceptSelf(),m);

                }else{
                    System.out.println("Node "+ myId+" will not send a message");
                }
            }else if(m.getMap().containsKey(MessageNameHelper.specialRequest)){

                int ID= Integer.parseInt(m.getMap().get(MessageNameHelper.ID));
                m= getSpecialMessage();
                sendUnicast(ID, m);

            }
        }
    }

    /**
     * dies wird in main aufgerufen wenn als terminierung ein kontrollvektor genutzt wird,
     * Eine NAchricht die den Kontrollvektor beinhaltet hat in der Map einen Key specialRequest welche die nachricht mit Kontrollvektor zeichnet
     * Dieser wird über den key controlVector erreicht
     * man hätte natürlich den kontrollvektor direkt als Wert vom Schlüssel specialRequest setzen können, aber nehmen wir an, aber als Value könnten da möglicherweise andere
     * Dinge sinnvoll sein
     * erhält die node eine Base Message also eine normale Nachricht von einer anderen Node, wird mit wahrscheinlichkeit x ebenfalls eine Nachricht an eine zufällige Node gesendet
     *
     * Der Kontrollvektor wird von der Kontrollnode zuerst an Base Node mit der Id 0 geschickt, von da an wird der Vektor an die folgende Node geschickt
     * Die letzte Node schickt den vektor zurück an die Kontrollnode, die prüft ob eine Terminierung gefunden wurde
     */
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

    ///////////////Only For doubleCountProcedure
    private int messagesSend;
    private int messagesReceived;
    private Message getSpecialMessage(){
        Message m= new Message();
        m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
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
    //////////////////end




    //////////////only for Vector Procedure
    private int [] localVector; //gets initiated at start of vector procedure with number of base nodes

    private void onReceive(){
        localVector[myId]--;
    }
    private void onSend(int ID){
        localVector[ID]++;
    }

    /**
     *
     * @param controlVector der kontrollvektor aus der Nachricht, wurde aus String ausgelesen
     * @return der verrechnete kontrollvektor für die nachricht, muss noch als String verpackt werden
     */
    private int[] calculateControlVector(int [] controlVector){

        if(localVector.length!= controlVector.length)
            throw new IllegalStateException("Vectors have to be the same size");
        for(int i=0; i< localVector.length; i++){

            controlVector[i]+= localVector[i];
        }
        return controlVector;
    }

    /**
     *
     * @param controlVectorAsStringFromMessage kontrollVektor als String aus der Message
     * @return verrechneter Kontrollvektor um an nächste BaseNode oder den controller weiterzuschicken falls diese node die letzte
     */

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
    //////////////////////end

    ///////////////Util
    public int getIDExceptSelf(){
        int id= myId;
        while(id== myId)
            id= rand.nextInt(numberOfNodes);
        return id;
    }


}
