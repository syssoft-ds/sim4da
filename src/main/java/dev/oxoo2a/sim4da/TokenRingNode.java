package dev.oxoo2a.sim4da;

import java.util.Random;

public class TokenRingNode extends Node{

    private int workTimes = 0;
    public TokenRingNode(int my_id) {
        super(my_id);
    }

    public TokenRingNode( int my_id, Clock clock ) { super(my_id, clock);}

    @Override
    protected void main() {
        int token_received = 0;
        int token_sent = 0;
        int next = (myId + 1) % numberOfNodes();

        Message u_cast = new Message().add("Sender",myId);

        // starts the token ring
        if (myId == 0)
            sendUnicast(next, u_cast);

        while (stillSimulating()) {
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message
            token_received++;

            work();

            sendUnicast(next, u_cast);
            token_sent++;
        }
        emit("Node %d: %d token received, %d token sent and worked %d times. Time %s ",myId,token_received,token_sent, workTimes, clock.getTime());
    }
    private void work(){
        Random r = new Random();
        int randomTimes = r.nextInt(5) ;
        for (int j = 0; j < randomTimes; j++) {
            clock.increase();
            workTimes++;
            emit("Node %d, Working %d. Time %s",myId,workTimes, clock.getTime());
        }
    }
}
