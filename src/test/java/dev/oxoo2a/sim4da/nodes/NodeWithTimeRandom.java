package dev.oxoo2a.sim4da.nodes;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.NodeWithTime;
import dev.oxoo2a.sim4da.Time;

import java.util.Random;

/***
 * A node with a logic clock that performs some random actions when a event is triggered
 */
public class NodeWithTimeRandom extends NodeWithTime {

    private final int n_nodes;

    public NodeWithTimeRandom(int id, Time time, int n_nodes){
        super(id, time);
        this.n_nodes = n_nodes;
    }



    /***
     * The inner event of the node is sleeping/pausing for some milliseconds
     */
    protected void innerEvent() {
        time.incrementMyTime();
        emit("%d: perform Inner Event at %s",myId,time.toString());
        try {
            Thread.sleep(2 * 100L);
        }
        catch (InterruptedException ignored) {}
    }

    /***
     * When a message is received a random number of events are performed.
     * By random the events can be INNER or SEND_MESSAGE.
     * At leased one SEND_MESSAGE must be performed so that the network continuous to do something.
     */
    protected void handleReceivingMessage() {
        boolean sendPerformed = false;

        Random rand = new Random();
        // Execute a random number of events
        int numberOfRandomEvents = rand.nextInt(3);
        for(int i = 0; i <= numberOfRandomEvents; i++) {
            // Get a random event type
            int randEvent_i = rand.nextInt(4);
            if (randEvent_i == 1){
                // send message to a random receiving node
                int randReceiverNodeId = rand.nextInt(n_nodes);
                if(randReceiverNodeId != myId){
                    sendUnicast(randReceiverNodeId, new Message());
                    sendPerformed = true;
                }
            }else{
                // do inner event
                innerEvent();
            }
        }

        // Make sure that at leased one message was send
        if(!sendPerformed){
            sendUnicast((myId+1) % n_nodes, new Message());
        }
    }


    @Override
    protected void main() {
        //Message m = new Message();
        if (myId == 0) {
            sendUnicast(1, new Message());
        }
        while (true) {
            // Listen for messages
            Network.Message m_raw = receive();
            if (m_raw == null) break;
            // Message received
            handleReceivingMessage();
        }
    }
}
