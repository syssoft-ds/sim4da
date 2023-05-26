package dev.oxoo2a.sim4da;

import java.util.ArrayList;

/***
 * Nodes withing the network can perform two kinds of events: An inner event or sending a message.
 * This class collects all the information for a event that is performed by a node, so that it can be analysed later.
 * The class only has the function to collect data with no logic.
 */
public class NodeEvent {

    public enum EventType { INNER, SEND_MESSAGE }
    // All events within the simulation
    private static ArrayList<NodeEvent> events = new ArrayList<>();

    private final int eventOrderId;
    private String performedTime;
    private final EventType type;
    /***
     * node that performed the event
     */
    private int executorNode;
    /***
     * only given for send_message. The node that received the message
     */
    private final int receiverNode;

    public NodeEvent(EventType type, int receiverNode){
        if(type == EventType.SEND_MESSAGE && receiverNode < 0)
            throw new IllegalArgumentException("No node given for message");
        this.type = type;
        this.receiverNode = receiverNode;
        this.eventOrderId = events.size();
        events.add(this);
    }

    /***
     * The event was performed -> update data in this class
     * @param executorNode - node that performed the event
     * @param time - local time of the performing node
     */
    public void performed(int executorNode, String time){
        this.executorNode = executorNode;
        this.performedTime = time;
    }

    public EventType getType() {
        return type;
    }

    public int getReceiverNode() {
        return receiverNode;
    }

    @Override
    public String toString() {
        return "ExampleEvent{" +
                "eventOrderId=" + eventOrderId +
                ", performedTime=" + performedTime +
                ", type=" + type +
                ", executorNode=" + executorNode +
                ", recieverNode=" + receiverNode +
                '}';
    }

    /***
     * prints all events within the simulation to the console
     */
    public static void printAllEvents(){
        for (NodeEvent e: events) {
            System.out.println(e.toString());
        }
    }
}
