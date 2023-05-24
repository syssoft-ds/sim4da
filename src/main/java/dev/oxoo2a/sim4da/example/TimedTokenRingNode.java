package dev.oxoo2a.sim4da.example;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.clock.ClockType;
import dev.oxoo2a.sim4da.clock.TimedNode;

/**
 * Example. Class extends TimedNode. Aside from adding the very first timestamp and specifying the ClockType in the
 * constructor, this class is identical to the TokenRingNode example;
 */
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
        }
        while (true){
            Network.Message m_raw = receive();

            if(m_raw == null){
                System.out.println("Message contents empty. Node dies.");
                break;
            }

            m = Message.fromJson(m_raw.payload);
            int counter = Integer.parseInt(m.query("counter"));
            emit("%d: counter == %d", myId, counter);
            counter++;
            m.add("counter", counter);
            sendUnicast((myId + 1) % numberOfNodes(),m);
        }
    }
}
