package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.oxoo2a.sim4da.Message.MessageType;

public class ValidateMessageCopies {
    private static final int numberOfNodes = 5;
    private static final int duration = 10;

    private class TestNode extends Node {
        public TestNode ( Simulator s, int id ) {
            super(s,id);
        }

        @Override
        public void run () {
            JsonSerializableMap m = new JsonSerializableMap();
            if (id == 0) {
                m.put("Sender", String.valueOf(id));
                m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                sendBroadcast(m);
            }
            while (isStillSimulating()) {
                Message message = receive();
                if (message==null) {
                    Assertions.assertTrue(isStillSimulating() == false);
                    break; // Null == Simulation time ends while waiting for a message
                }
                Assertions.assertTrue(message.getReceiverId() == id);
                Assertions.assertTrue(message.getType() == MessageType.BROADCAST);
                m = JsonSerializableMap.fromJson(message.getPayload());
                int sender_id = Integer.parseInt(m.get("Sender"));
                Assertions.assertTrue(sender_id == message.getSenderId());
                int candidate_id = Integer.parseInt(m.get("Candidate"));
                Assertions.assertTrue(candidate_id == message.getReceiverId());
                if (candidate_id == id) {
                    m.put("Sender",String.valueOf(id));
                    m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                    sendBroadcast(m);
                    }
            }
        }
    }

    @Test
    public void areMessagesCopied () {
        Simulator s = new Simulator(numberOfNodes, "amc", true, true, System.out);
        for (int id = 0; id<numberOfNodes; id++) {
            Node n = new TestNode(s, id);
            s.attachNode(n);
        }
        try {
            s.runSimulation(duration);
        } catch (InstantiationException ignored) {
            Assertions.fail("Not all nodes instantiated");
        }

    }
}
