package dev.oxoo2a.sim4da;

import java.util.Random;

/***
 * A node with a logic clock that performs some random actions when a event is triggered
 */
public class NodeWithTimeRandom extends NodeWithTime{

    private final int n_nodes;

    public NodeWithTimeRandom(int id, Time time, int n_nodes){
        super(id, time);
        this.n_nodes = n_nodes;
    }

    /***
     * when the simulation starts the first node sends a message to the second
     */
    @Override
    protected void handleStart() {
        if (myId == 0) {
            performEvent(new NodeEvent(NodeEvent.EventType.SEND_MESSAGE, 1));
        }
    }

    /***
     * The inner event of the node is sleeping/pausing for some milliseconds
     */
    @Override
    protected void handleInnerEvent() {
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
    @Override
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
                    performEvent(new NodeEvent(NodeEvent.EventType.SEND_MESSAGE, randReceiverNodeId));
                    sendPerformed = true;
                }
            }else{
                // do inner event
                performEvent(new NodeEvent(NodeEvent.EventType.INNER, -1));
            }
        }

        // Make sure that at leased one message was send
        if(!sendPerformed){
            performEvent(new NodeEvent(NodeEvent.EventType.SEND_MESSAGE, (myId+1) % n_nodes));
        }
    }


}
