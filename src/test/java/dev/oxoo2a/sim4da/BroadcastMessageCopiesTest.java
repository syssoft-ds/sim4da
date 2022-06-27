package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Simulator.TimestampType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.oxoo2a.sim4da.Message.MessageType;

public class BroadcastMessageCopiesTest {
    
    private static final int NUMBER_OF_NODES = 5;
    private static final int DURATION = 2;
    
    @Test
    public void areMessagesCopied () {
        Simulator s = new Simulator(NUMBER_OF_NODES, TimestampType.EXTENDED_LAMPORT, 0, "amc", true, System.out, true);
        for (int id = 0; id<NUMBER_OF_NODES; id++) {
            Node n = new TestNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(DURATION);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }
    }
    
    private static class TestNode extends Node {
        private TestNode(Simulator s, int id) {
            super(s, id);
        }
        @Override
        public void run() {
            JsonSerializableMap m = new JsonSerializableMap();
            if (id==0) {
                m.put("Sender", String.valueOf(id));
                m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                sendBroadcast(m);
            }
            while (isStillSimulating()) {
                Message message = receive();
                if (message==null) {
                    Assertions.assertFalse(isStillSimulating());
                    break; // null==simulation time ends while waiting for a message
                }
                Assertions.assertEquals(message.getReceiverId(), id);
                Assertions.assertSame(message.getType(), MessageType.BROADCAST);
                m = JsonSerializableMap.fromJson(message.getPayload());
                int senderId = Integer.parseInt(m.get("Sender"));
                Assertions.assertEquals(senderId, message.getSenderId());
                int candidateId = Integer.parseInt(m.get("Candidate"));
                if (candidateId==id) {
                    Assertions.assertEquals(candidateId, message.getReceiverId());
                    m.put("Sender", String.valueOf(id));
                    m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                    sendBroadcast(m);
                }
            }
        }
    }
}
