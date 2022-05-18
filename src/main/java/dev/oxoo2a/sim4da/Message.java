package dev.oxoo2a.sim4da;

public class Message {
    
    public int senderId;
    public int receiverId;
    public MessageType type;
    public String payload;
    
    public Message(int senderId, int receiverId, MessageType type, String payload) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.payload = payload;
    }
    
    @Override
    public String toString() {
        return "Network::Message(sender="+senderId+",receiver="+receiverId+","
                +(type==MessageType.BROADCAST ? "Broadcast" : "Unicast")+",payload=<"+payload+">)";
    }
    
    public enum MessageType {
        UNICAST, BROADCAST
    }
}
