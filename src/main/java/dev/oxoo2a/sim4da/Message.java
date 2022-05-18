package dev.oxoo2a.sim4da;

public class Message {
    
    private final int senderId;
    private final int receiverId;
    private final MessageType type;
    private final String payload;
    
    public Message(int senderId, int receiverId, MessageType type, String payload) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.payload = payload;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public String getPayload() {
        return payload;
    }
    
    @Override
    public String toString() {
        return "Message(sender="+senderId+",receiver="+receiverId+","
                +(type==MessageType.BROADCAST ? "Broadcast" : "Unicast")+",payload=<"+payload+">)";
    }
    
    public enum MessageType {
        UNICAST, BROADCAST
    }
}
