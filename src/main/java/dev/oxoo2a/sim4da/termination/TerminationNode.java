package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.Random;

public class TerminationNode extends Node {


    private final int intervallBetweenNewProcedure;
    private long timeSinceLastUpdate;
    private CurrentState currentState;
    private final int numberOfBaseNodes;

    private final TerminationType terminationType;


    public TerminationNode(int my_id, int numberOfBaseNodes, int intervallBetweenNewProcedure, TerminationType terminationType) {
        super(my_id);
        currentState= CurrentState.IDLE;
        this.numberOfBaseNodes= numberOfBaseNodes;
        this.intervallBetweenNewProcedure= intervallBetweenNewProcedure;
        Random rand= new Random(11111+my_id);
        timeSinceLastUpdate= rand.nextInt(1000);
        this.terminationType=terminationType;

    }


    @Override
    protected void main() {

        switch (terminationType){
            case countProcedure -> actAsCountProcedureNode();
            case vector -> actAsControlVectorNode();
        }

 }

    /**
     * possible states
     */
    private enum CurrentState{
        //used for both
        IDLE,
        //used for double count procedure
        waitingForAnwersOnFirstRequest,
        waitingForAnswersOnSecondRequest,
        //used for vector
        waiting

    }

    ////// double count
    private int firstIterMessagesSend=0;
    private int firstIterMessagesReceived=0;
    private int secondIterMessagesSend=0;
    private int secondIterMessagesReceived=0;
    private int answerReceivedOnFirstIter=0;
    private int answersReceivedOnSecondIter=0;

    /**
     * wenn das in der main aufgerufen wird, verhält sich die Terminierungs node entlang dem doppelzählverfahren
     * Sie kann IDLE sein, auf antworten der ersten iteration oder auf antworten der zweiten iteration warten
     * Ist sie IDLE, muss das zeitintervall x verstrichen sein, seit sie die letzte terminierungsprüfung beendet hat, bevor sie wieder mit Phase 1 startet
     * Wartet sie auf antworten aus Phase 1 oder Phase 2 (nur eins ist der Fall), darf sie auf Nachrichten warten
     * demnach checkt sie jedes mal wenn sie eine Nachricht empfängt ob dies die letzte nachricht war, sodass von phase eins oder Phase 2 (nur eines ist der Fall) alle nachrichten da sind
     * je nachdem startet sie Phase 2 oder checkt die Terminierung
     * Nachrichten die von den Base Nodes an sie zurückgehen müssen immer den Key specialAnswer enthalten, desWeiteren iteration, messagesSend und messagesReceived
      */


    public void actAsCountProcedureNode(){

        Message m;

        while (true){

            if(currentState== CurrentState.waitingForAnwersOnFirstRequest|| currentState== CurrentState.waitingForAnswersOnSecondRequest) {
                Network.Message m_raw = receive();
                if (m_raw == null) break;
                m = Message.fromJson(m_raw.payload);
                if(m.getMap().containsKey(MessageNameHelper.specialAnswer)){
                    String send= m.getMap().get(MessageNameHelper.messagesSend);
                    String received= m.getMap().get(MessageNameHelper.messagesReceived);
                    addAccordingToState(send, received);
                }
            }
            if(currentState== CurrentState.IDLE && TimeManager.getCurrentSimTime()- timeSinceLastUpdate> intervallBetweenNewProcedure){
                System.out.println("Terminator "+(myId-numberOfBaseNodes)+
                        ": requesting Iteration one: sim Time"+ TimeManager.getCurrentSimTime());
                currentState= CurrentState.waitingForAnwersOnFirstRequest;
                sendRequestToAll();

            }else if(currentState== CurrentState.waitingForAnwersOnFirstRequest){
                if(receivedAllMessages()){

                    currentState= CurrentState.waitingForAnswersOnSecondRequest;
                    answerReceivedOnFirstIter=0;
                    sendRequestToAll();

                }else{
                    System.out.println("Not all answers received on First, currently: "+ answerReceivedOnFirstIter);
                }
            }else if(currentState== CurrentState.waitingForAnswersOnSecondRequest){
                if(receivedAllMessages()){
                    timeSinceLastUpdate= TimeManager.getCurrentSimTime();
                    currentState= CurrentState.IDLE;
                    answersReceivedOnSecondIter=0;
                    checkForTermination();
                }else{
                    System.out.println("Not all answers received on Second, currently: "+ answersReceivedOnSecondIter);

                }

            }
        }
    }

    /**
     *  @param send von node x insgesamt gesendete Nachrichten
     * @param received von node x insgesamt empfangene nachrichten
     */

    private void addAccordingToState(String send, String received){
        if(currentState==CurrentState.waitingForAnwersOnFirstRequest){
            answerReceivedOnFirstIter++;
            if(answerReceivedOnFirstIter> numberOfBaseNodes)
                throw new IllegalStateException("We should not receive more answers than there are nodes for one iteration");
            firstIterMessagesSend+= Integer.parseInt(send);
            firstIterMessagesReceived+= Integer.parseInt(received);
        }else if(currentState==CurrentState.waitingForAnswersOnSecondRequest){
            answersReceivedOnSecondIter++;
            if(answersReceivedOnSecondIter> numberOfBaseNodes)
                throw new IllegalStateException("We should not receive more answers than there are nodes for one iteration");
            secondIterMessagesSend+= Integer.parseInt(send);
            secondIterMessagesReceived+= Integer.parseInt(received);

        }
    }

    /**
     * @return true wenn alle Antworten auf Anfrage angekommen sind, sonst false, wenn true dann wird terminierung geprueft
     */
    private boolean receivedAllMessages(){
        switch (currentState){
            case waitingForAnwersOnFirstRequest -> {return answerReceivedOnFirstIter== numberOfBaseNodes;
            }
            case waitingForAnswersOnSecondRequest -> {return answersReceivedOnSecondIter==numberOfBaseNodes;
            }
            case IDLE -> throw new IllegalStateException("This should not be called on IDLE");

        }
        throw new IllegalStateException("No Case defined");
    }


    /**
     * sende Anfrage für Statusmeldung an alle
     */
    private void sendRequestToAll(){
        Message m= new Message();
                m.getMap().put(MessageNameHelper.specialRequest, "true");
                m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
        for (int i=0; i< numberOfBaseNodes; i++){
            sendUnicast(i, m);
        }
    }

    /**
     * Terminierungs Check beim Doppelzaehlverfahren
     */
    private void checkForTermination(){
        if(firstIterMessagesReceived== firstIterMessagesSend&& firstIterMessagesReceived== secondIterMessagesReceived&& secondIterMessagesReceived==secondIterMessagesSend){
            System.out.println("Found termination by terminator node : " + (myId-numberOfBaseNodes)+
                    "\n firstIterMessagesSend: "+ firstIterMessagesSend+
                    "\nfirstIterMessagesReceived: "+firstIterMessagesReceived+
                    "\nsecondIterMessagesSend: " + secondIterMessagesSend+
                    "\nsecondIterMessagesReceived:"+ secondIterMessagesReceived);
            System.exit(0);

        }else{
            System.out.println("Did not terminate as values are the following (done by terminator node at sim Time +"+ TimeManager.getCurrentSimTime()+": "+ (myId-numberOfBaseNodes)+")" +
                    "\n firstIterMessagesSend: "+ firstIterMessagesSend+
                    "\nfirstIterMessagesReceived: "+firstIterMessagesReceived+
                    "\nsecondIterMessagesSend: " + secondIterMessagesSend+
                    "\nsecondIterMessagesReceived:"+ secondIterMessagesReceived);
        }
        firstIterMessagesSend=0;
        firstIterMessagesReceived=0;
        secondIterMessagesReceived=0;
        secondIterMessagesSend=0;
    }
    ////////////////end

    ////////////////controlVector

    /**
     * wenn das in der main aufgerufen wird, verhält sich die kontroll node entlang des kontrollvektorverfahrens
     * ist die node IDLE wartet sie auf keine Antworten und schickt wenn das itnervall x seit dem ende des letzten verfahrens rum ist einen neuen kontrollvektor los
     * ist die node am warten hat sie irgendwann einen Kontrollvektor an die erste Base Node mit der ID 0 gesendet und wartet nun auf die antwort der letzten Base Node,
     * die den Kontrollvektor wieder zurücksendet um dann zu schauen ob dies der Nullvektor ist
     */
    public void actAsControlVectorNode(){
        String controlVectorInstance = buildControlVectorString();
        Message m;
        while(true) {

            if (currentState == CurrentState.IDLE && TimeManager.getCurrentSimTime() - timeSinceLastUpdate > intervallBetweenNewProcedure) {
                System.out.println("Terminator "+(myId-numberOfBaseNodes)+
                        ": Sending controlVector: sim Time"+ TimeManager.getCurrentSimTime());
                currentState = CurrentState.waiting;
                m = new Message();
                m.getMap().put(MessageNameHelper.specialRequest, "true");
                m.getMap().put(MessageNameHelper.controlVector, controlVectorInstance);
                m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
                sendUnicast(0, m);

            } else if (currentState == CurrentState.waiting) {

                Network.Message m_raw = receive();
                if (m_raw == null) break;
                m = Message.fromJson(m_raw.payload);

                if(m.getMap().containsKey(MessageNameHelper.specialAnswer)){
                    timeSinceLastUpdate= TimeManager.getCurrentSimTime();
                    currentState= CurrentState.IDLE;
                    String vector= m.getMap().get(MessageNameHelper.controlVector);
                    isNullVector(vector);
                }else{
                    throw new IllegalStateException("Something went wrong");
                }
            }
        }
    }


    /**
     * have to find agreement on how the String is structured here it is + or - followed by a delimiter : followed by the value followed by a delimiter ;
     * @return the string consisting of zeros
     */
    private String buildControlVectorString(){
        String controlVectorAsString="";
        for(int i=0; i< numberOfBaseNodes; i++){
            controlVectorAsString+= "+:"+ 0 +";";
        }
        return controlVectorAsString;
    }

    /**
     * checks if a vector is the nullvector
     * @param vector controllvector that was send by the last node of all base nodes after this has visited all other base nodes
     */
    private void isNullVector(String vector){
        int[] vectorAsInt= Utils.getVectorFromString(vector, numberOfBaseNodes);
        for(int i =0 ; i< vectorAsInt.length; i++) {
            if (vectorAsInt[i] != 0) {
                System.out.println("Control Vector is not 0 for Node "+ i);
                System.out.println("Size "+ vectorAsInt.length);
                return;
            }
        }
        System.out.println("Control Vector is nullVector, termination found by Terminator "+ (myId-numberOfBaseNodes));
        System.exit(0);
    }

    //////////////////end

}
