package dev.oxoo2a.sim4da;

/***
 * Node in the network that has a logic clock
 * @author Tessa Steinigke
 */
public abstract class NodeWithTime extends Node {

    protected final Time time; // manages the time according to the rules in the class of the logic clock

    public NodeWithTime(int my_id, Time time) {
        super(my_id);
        this.time = time;
    }

    protected void sendUnicast ( int receiver_id, String m ) {
        //time.incrementMyTime();
        //super.sendUnicast(receiver_id, m);
    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        time.incrementMyTime();
        emit("%d: perform Unicast at %s",myId,time.toString());
        super.sendUnicast(receiver_id, new MessageWithTime(m, time));
    }

    protected void sendBroadcast ( String m ) {
        //time.incrementMyTime();
        //super.sendBroadcast(m);
    }

    protected void sendBroadcast ( Message m ) {
        time.incrementMyTime();
        emit("%d: perform Broadcast at %s",myId,time.toString());
        super.sendBroadcast(new MessageWithTime(m, time));
    }

    protected Network.Message receive () {
        Network.Message m_raw = super.receive();
        if (m_raw != null) {
            MessageWithTime m = MessageWithTime.fromJson(m_raw.payload);
            // Update the time with the new time information given in the message
            time.updateTime(m.getTime());

            String m_type = m_raw.type == Network.MessageType.BROADCAST ? "Broadcast" : "Unicast";
            emit("%d: recieve %s at %s", myId, m_type,time.toString());
        }
        return m_raw;
    }

}
