package dev.oxoo2a.sim4da;

/***
 * Node in the network that has a logic clock
 * @author Tessa Steinigke
 */
public abstract class NodeWithTime extends Node {

    private Time time; // manages the time according to the rules in the class of the logic clock

    public NodeWithTime(int my_id, Time time) {
        super(my_id);
        this.time = time;
    }

    /***
     * An event is triggered and executed
     * @param event - event to execute
     */
    public void performEvent(NodeEvent event){
        // Update the time
        time.incrementMyTime();
        event.performed(myId, time.toString());
        // Execute event
        if(event.getType() == NodeEvent.EventType.SEND_MESSAGE){
            // Send Message
            emit("%d: perform send_message at %s",myId,time.toString());
            Message m = new Message();
            m.add("time",time.toString()); // In each message the time needs to be send
            sendUnicast(event.getReceiverNode(),m);
        }else{
            // Do some inner event
            emit("%d: perform inner_event at %s",myId,time.toString());
            handleInnerEvent();
        }
    }

    // Nodes within the network can handle events differently.
    // That's why this class is abstract and child classes can define their own reactions to events.

    /***
     * Defines the inner event of the node
     */
    protected abstract void handleInnerEvent();

    /***
     * Defines what happens after a message was received
     */
    protected abstract void handleReceivingMessage();

    /***
     * Defines what happens at the start of the simulation
     */
    protected abstract void handleStart();

    /***
     * A message was received from a different node
     * @param m - Message from the network
     */
    private void receivedMessage(Message m){
        // Update the time with the new time information given in the message
        time.updateTime(m.query("time"));

        // Do something with message
        handleReceivingMessage();
    }

    @Override
    protected void main() {
        handleStart();
        while (true) {
            // Listen for messages
            Network.Message m_raw = receive();
            if (m_raw == null) break;
            Message m = Message.fromJson(m_raw.payload);
            receivedMessage(m);
        }
    }
}
