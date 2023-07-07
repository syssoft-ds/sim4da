package dev.oxoo2a.sim4da;

/***
 * Node in the network that has a logic clock
 * @author Tessa Steinigke
 */
public abstract class NodeWithTime extends Node {

    protected final Time time; // manages the time according to the rules in the class of the logic clock
    private final boolean logTime;

    public NodeWithTime(int my_id, Time time) {
        this(my_id, time, false);
    }
    public NodeWithTime(int my_id, Time time, boolean logTime) {
        super(my_id);
        this.time = time;
        this.logTime = logTime;
    }

    protected void sendUnicast ( int receiver_id, String m ) {
        time.incrementMyTime();
        emit("Warning: Can not update time in message!");
        if(logTime) emit("%d: perform Unicast at %s",myId,time.toString());
        super.sendUnicast(receiver_id, m); // WARNING: can not update time in message
    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        time.incrementMyTime();
        if(logTime) emit("%d: perform Unicast at %s",myId,time.toString());
        super.sendUnicast(receiver_id, new Message(m, time));
    }

    protected void sendBroadcast ( String m ) {
        time.incrementMyTime();
        emit("Warning: Can not update time in message!");
        if(logTime) emit("%d: perform Broadcast at %s",myId,time.toString());
        super.sendBroadcast(m); // WARNING: can not update time in message
    }

    protected void sendBroadcast ( Message m ) {
        time.incrementMyTime();
        if(logTime) emit("%d: perform Broadcast at %s",myId,time.toString());
        super.sendBroadcast(new Message(m, time));
    }

    protected Network.Message receive () {
        Network.Message m_raw = super.receive();
        if (m_raw != null) {
            Message m = Message.fromJson(m_raw.payload);
            // Update the time with the new time information given in the message
            if(m.hasTime())
                time.updateTime(m.getTime());

            if(logTime) {
                String m_type = m_raw.type == Network.MessageType.BROADCAST ? "Broadcast" : "Unicast";
                emit("%d: receive %s at %s", myId, m_type,time.toString());
            }
        }
        return m_raw;
    }

}
