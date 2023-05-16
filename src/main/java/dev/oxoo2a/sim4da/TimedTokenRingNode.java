package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.clock.ClockType;
import dev.oxoo2a.sim4da.clock.TimedNode;

public class TimedTokenRingNode extends TimedNode {
    public TimedTokenRingNode(int my_id, ClockType type) {
        super(my_id, type);
    }

    @Override
    public void main(){
        Message m = new Message();

        if(myId == 0){
            m.add("counter", 0);
            sendUnicast(1, m);
            m.add("%T"+myId, 0);
            System.out.println("sending Message");
            System.out.println(m);
        }
        while (true){
            Network.Message m_raw = receive();

            if(m_raw == null) break;

            System.out.println("Class");
            System.out.println(m_raw.getClass());
            m = Message.fromJson(m_raw.payload);
            System.out.println("message in node loop");
            System.out.println(m);
            int counter = Integer.parseInt(m.query("counter"));
            emit("%d: counter == %d", myId, counter);
            counter++;
            m.add("counter", counter);
            sendUnicast((myId + 1) % numberOfNodes(),m);
        }
    }
}
