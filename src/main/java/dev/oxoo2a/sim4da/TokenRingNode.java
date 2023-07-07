package dev.oxoo2a.sim4da;

public class TokenRingNode extends Node{
    public TokenRingNode(int my_id) {
        super(my_id);
    }

    @Override
    protected void main() {
        Message m = new Message();
        if(myId == 0){
            m.add("counter", 0);
            sendUnicast(1, m);
        }
        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) break;
            m = Message.fromJson(m_raw.payload);
            int counter = Integer.parseInt(m.query("counter"));
            emit("%d: counter == %d", myId, counter);
            counter++;
            m.add("counter", counter);
            sendUnicast((myId + 1) % numberOfNodes(),m);
        }
    }
}
