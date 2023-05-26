package dev.oxoo2a.sim4da;

public class TokenRingNode extends Node {

    public TokenRingNode(int my_id, VectorClock clock, Tracer tracer) {
        super(my_id,clock, tracer);

    }
    @Override
    public void setClock(Clock clock) {
        this.clock = clock;

    }

    @Override
    protected void main() {
        Message m = new Message();
        int timestamp = clock.getTime();

        if (myId == 0) {
            m.add("counter", 0);
            sendUnicast(1, m, timestamp);
        }
        while (stillSimulating()) {
            Network.Message m_raw = receive();
            if (m_raw == null)
                break;
            m = Message.fromJson(m_raw.payload);


            String counterStr = m.query("counter");
            int counter;
            if (counterStr != null) {
                counter = Integer.parseInt(counterStr);
                counter++;
            } else {
                counter = 0;
            }

            m.add("counter", String.valueOf(counter));

            timestamp = clock.getTime();


            sendUnicast((myId + 1) % numberOfNodes(), m, timestamp);
        }
    }
}
