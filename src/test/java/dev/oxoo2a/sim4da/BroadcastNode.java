package dev.oxoo2a.sim4da;

import java.util.Random;

public class BroadcastNode extends Node {
    // The superclass needs its ID
    public BroadcastNode ( int id ) {
        super(id);
    }

    public void main () {
        Random r = new Random();
        int broadcasts_received = 0;
        int broadcasts_sent = 0;
        int loops = 0;
        // Create a message with a random candidate to send the next broadcast
        Message m_broadcast = new Message().add("Sender",myId).add("Candidate",r.nextInt(numberOfNodes()));
        sendBroadcast(m_broadcast);
        while (stillSimulating()) {
            loops++;
            emit("Node %d, Loop %d",myId,loops);
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message
            broadcasts_received++;
            // The following printf shows the elements of Network.Message except the message type unicast or broadcast
            // System.out.printf("%d: from %d, payload=<%s>\n",myId(),m_raw.sender_id,m_raw.payload);
            // JSON encoded messages must be deserialized into a Message object
            Message m_json = Message.fromJson(m_raw.payload);
            int c = Integer.parseInt(m_json.query("Candidate"));
            // Who's the next candidate for sending a broadcast message. There's also a small probability, that we
            // send a broadcast message anyway :-)
            if ((c == myId) || (r.nextInt(100) < 5)) {
                // The next sender for a broadcast message is selected randomly
                m_broadcast.add("Candidate",r.nextInt(numberOfNodes()));
                sendBroadcast(m_broadcast);
                broadcasts_sent++;
            }
        }
        emit("%d: %d broadcasts received and %d broadcasts sent",myId,broadcasts_received,broadcasts_sent);
    }
}
