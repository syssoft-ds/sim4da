package dev.oxoo2a.sim4da;

public class BroadcastNode extends Node {
    
    public BroadcastNode(Simulator simulator, int id) {
        super(simulator, id);
    }
    
    @Override
    public void run() {
        // System.out.printf("This is node %d\n", id);
        // Create a message with a random candidate to send the next broadcast
        JsonSerializableMap broadcastContent = new JsonSerializableMap();
        broadcastContent.put("Sender", String.valueOf(id));
        broadcastContent.put("Candidate", String.valueOf(getRandom().nextInt(getNumberOfNodes())));
        sendBroadcast(broadcastContent);
        int broadcastsReceived = 0;
        int broadcastsSent = 0;
        while (isStillSimulating()) {
            Message message = receive();
            if (message==null) break; // Null == Simulation time ends while waiting for a message
            broadcastsReceived++;
            // The following printf shows the elements of Message except the message type (unicast or broadcast)
            // System.out.printf("%d: from %d, payload=<%s>\n", id, message.senderId, message.payload);
            // JSON encoded messages must be deserialized into a Message object
            JsonSerializableMap receivedContent = JsonSerializableMap.fromJson(message.getPayload());
            int c = Integer.parseInt(receivedContent.get("Candidate"));
            // Who's the next candidate for sending a broadcast message. There's also a small probability, that we
            // send a broadcast message anyway :-)
            if (c==id || getRandom().nextInt(100)<5) {
                // The next sender for a broadcast message is selected randomly
                broadcastContent.put("Candidate", String.valueOf(getRandom().nextInt(getNumberOfNodes())));
                sendBroadcast(broadcastContent);
                broadcastsSent++;
            }
        }
        System.out.printf("%d: %d broadcasts received and %d broadcasts sent\n", id,
                broadcastsReceived, broadcastsSent);
    }
}
