package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.oxoo2a.sim4da.Message.MessageType;

public class ValidateMessageCopies {
    private static final int numberOfNodes = 10;
    private static final int duration = 2;

    private class TestNode extends Node {
        public TestNode ( Simulator s, int id ) {
            super(s,id);
        }

        @Override
        public void run () {
            JsonSerializableMap m = new JsonSerializableMap();
            m.put("Sender", String.valueOf(id));
            m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
            sendBroadcast(m);
            while (isStillSimulating()) {
                Message message = receive();
                if (message==null) break; // Null == Simulation time ends while waiting for a message
                Assertions.assertTrue(message.receiverId == id+1);
                Assertions.assertTrue(message.type == MessageType.BROADCAST);
                m = JsonSerializableMap.fromJson(message.payload);
                int sender_id = Integer.parseInt(m.get("Sender"));
                Assertions.assertTrue(sender_id == message.senderId);
                m.put("Sender",String.valueOf(id));
                m.put("Candidate", Integer.toString((id+1) % getNumberOfNodes()));
                sendBroadcast(m);
            }
        }
    }

    @Test
    public void areMessagesCopied () {
        Assertions.assertTrue(numberOfNodes == 10);
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
