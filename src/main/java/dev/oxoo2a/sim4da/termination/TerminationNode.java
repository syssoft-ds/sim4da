package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.TokenRingNode;

import java.util.Random;

public class TerminationNode extends Node {


    private final int intervallBetweenNewProcedure;
    private long timeSinceLastUpdate;
    private CurrentState currentState;
    private final int numberOfBaseNodes;
    private int firstIterMessagesSend=0;
    private int firstIterMessagesReceived=0;
    private int secondIterMessagesSend=0;
    private int secondIterMessagesReceived=0;
    private int answerReceivedOnFirstIter=0;
    private int answersReceivedOnSecondIter=0;
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

    private enum CurrentState{
        IDLE,
        waitingForAnwersOnFirstRequest,
        waitingForAnswersOnSecondRequest
    }

    private void addAccordingToIteration(String iteration, String send, String received){
        if(iteration.equals("first")){
            answerReceivedOnFirstIter++;
            if(answerReceivedOnFirstIter> numberOfBaseNodes)
                throw new IllegalStateException("We should not receive more answers than there are nodes for one iteration");
            firstIterMessagesSend+= Integer.parseInt(send);
            firstIterMessagesReceived+= Integer.parseInt(received);
        }else if(iteration.equals("second")){
            answersReceivedOnSecondIter++;
            if(answersReceivedOnSecondIter> numberOfBaseNodes)
                throw new IllegalStateException("We should not receive more answers than there are nodes for one iteration");
            secondIterMessagesSend+= Integer.parseInt(send);
            secondIterMessagesReceived+= Integer.parseInt(received);

        }
    }
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

    private void sendRequestToAll(String iteration){
        Message m= new Message();
                m.getMap().put(MessageNameHelper.specialRequest, "true");
                m.getMap().put(MessageNameHelper.iteration, iteration);
                m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
        for (int i=0; i< numberOfBaseNodes; i++){
            sendUnicast(i, m);
        }
    }

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

    public void actAsControlVectorNode(){
        controlVectorInstance = buildControlVectorString();
        Message m;
        while(true) {

            if (currentState == CurrentState.IDLE && TimeManager.getCurrentSimTime() - timeSinceLastUpdate > intervallBetweenNewProcedure) {
                System.out.println("Sending controlVector");
                currentState = CurrentState.waitingForAnwersOnFirstRequest;
                m = new Message();
                m.getMap().put(MessageNameHelper.specialRequest, "true");
                m.getMap().put(MessageNameHelper.controlVector, controlVectorInstance);
                m.getMap().put(MessageNameHelper.ID, String.valueOf(myId));
                sendUnicast(0, m);

            } else if (currentState == CurrentState.waitingForAnwersOnFirstRequest) {

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
    String controlVectorInstance;

    private String buildControlVectorString(){
        String controlVectorAsString="";
        for(int i=0; i< numberOfBaseNodes; i++){
            controlVectorAsString+= "+:"+ 0 +";";
        }
        return controlVectorAsString;
    }
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
    public void actAsCountProcedureNode(){

        Message m = new Message();

        while (true){

            if(currentState== CurrentState.waitingForAnwersOnFirstRequest|| currentState== CurrentState.waitingForAnswersOnSecondRequest) {
                Network.Message m_raw = receive();
                if (m_raw == null) break;
                m = Message.fromJson(m_raw.payload);
                if(m.getMap().containsKey(MessageNameHelper.specialAnswer)){
                    String iteration= m.getMap().get(MessageNameHelper.iteration);
                    String send= m.getMap().get(MessageNameHelper.messagesSend);
                    String received= m.getMap().get(MessageNameHelper.messagesReceived);
                    addAccordingToIteration(iteration, send, received);
                }


            }
            if(currentState== CurrentState.IDLE && TimeManager.getCurrentSimTime()- timeSinceLastUpdate> intervallBetweenNewProcedure){
                System.out.println("Am IDLE, sending first Request");
                currentState= CurrentState.waitingForAnwersOnFirstRequest;
                sendRequestToAll("first");

            }else if(currentState== CurrentState.waitingForAnwersOnFirstRequest){
                if(receivedAllMessages()){
                    System.out.println("Am waiting first request but received All, sending second now" +
                            "\n values are "+
                            "\n firstIterMessagesSend: "+ firstIterMessagesSend+
                            "\nfirstIterMessagesReceived: "+firstIterMessagesReceived);
                    currentState= CurrentState.waitingForAnswersOnSecondRequest;
                    answerReceivedOnFirstIter=0;
                    sendRequestToAll("second");

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
}
