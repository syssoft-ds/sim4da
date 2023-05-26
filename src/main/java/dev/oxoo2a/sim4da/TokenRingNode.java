package dev.oxoo2a.sim4da;

public class TokenRingNode extends Node {

    public TokenRingNode(int my_id) {
        super(my_id);
    }

    @Override
    protected void main() {
        Message m = new Message();
        if (myId == 0) {
            // Send first message
            m.add("counter",0);
            sendUnicast(1,m);
        }
        while (true) {
            // Listen for messages
            Network.Message m_raw = receive();
            if (m_raw == null) break;
            // Message received
            m = Message.fromJson(m_raw.payload);
            int counter = Integer.parseInt(m.query("counter"));
            emit("%d: counter==%d",myId,counter);
            // Token increased
            counter++;
            m.add("counter",counter);
            // Next message send
            sendUnicast((myId+1) % numberOfNodes(),m);
        }
    }

}
