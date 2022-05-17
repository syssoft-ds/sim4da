package dev.oxoo2a.sim4da;

import java.util.Random;

public class BroadcastNode extends Node {
    
    // The superclass needs its ID
    public BroadcastNode(int id) {
        super(id);
    }
    
    @Override
    public void main() {
        Random r = new Random();
        // System.out.printf("This is node %d\n", myId());
        // Create a message with a random candidate to send the next broadcast
        JsonSerializableMap m_broadcast = new JsonSerializableMap();
        m_broadcast.put("Sender", String.valueOf(myId));
        m_broadcast.put("Candidate", String.valueOf(r.nextInt(getNumberOfNodes())));
        sendBroadcast(m_broadcast);
        int broadcasts_received = 0;
        int broadcasts_sent = 0;
        while (stillSimulating()) {
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Simulation time ends while waiting for a message
            broadcasts_received++;
            // The following printf shows the elements of Network.Message except the message type unicast or broadcast
            // System.out.printf("%d: from %d, payload=<%s>\n",myId(),m_raw.sender_id,m_raw.payload);
            // JSON encoded messages must be deserialized into a Message object
            JsonSerializableMap m_json = JsonSerializableMap.fromJson(m_raw.payload);
            int c = Integer.parseInt(m_json.get("Candidate"));
            // Who's the next candidate for sending a broadcast message. There's also a small probability, that we
            // send a broadcast message anyway :-)
            if (c == myId || r.nextInt(100) < 5) {
                // The next sender for a broadcast message is selected randomly
                m_broadcast.put("Candidate", String.valueOf(r.nextInt(getNumberOfNodes())));
                sendBroadcast(m_broadcast);
                broadcasts_sent++;
            }
        }
        System.out.printf("%d: %d broadcasts received and %d broadcasts sent\n",myId,broadcasts_received,broadcasts_sent);
    }
}
