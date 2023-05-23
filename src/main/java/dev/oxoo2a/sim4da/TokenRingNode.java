package dev.oxoo2a.sim4da;

public class TokenRingNode extends Node {


    public TokenRingNode(int my_id, Clock clock, Tracer tracer) {
        super(my_id,clock, tracer);

    }
    @Override
    public void setClock(Clock clock) {
        this.clock = clock;

    }

    @Override
    protected void main() {
        Message m = new Message();
        int timestamp = clock.getValue();
        if (myId == 0) {
            m.add("counter", 0);
            sendUnicast(1, m, timestamp);
        }
        while (true) {
            Network.Message m_raw = receive();
            if (m_raw == null)
                break;
            m = Message.fromJson(m_raw.payload);


            int counter = Integer.parseInt(m.query("counter"));
            counter++;
            m.add("counter", counter);


            sendUnicast((myId + 1) % numberOfNodes(), m, timestamp);
        }
    }
}
