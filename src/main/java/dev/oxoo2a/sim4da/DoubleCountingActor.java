package dev.oxoo2a.sim4da;

import java.util.HashSet;
import java.util.Set;

public class DoubleCountingActor {
    private Set<Integer> receivedMessages;
    private Set<Integer> terminatedActors;
    private int count;

    public DoubleCountingActor() {
        receivedMessages = new HashSet<>();
        terminatedActors = new HashSet<>();
        count = 0;
    }

    public void receiveMessage(int senderId) {
        receivedMessages.add(senderId);
        System.out.println("Actor " + senderId + " received a message.");

        // Überprüfe auf Terminierung anhand der empfangenen Nachrichten
        if (receivedMessages.size() == count) {
            actorTerminated(1);
        }
    }

    public void actorTerminated(int numTerminatedActors) {
        for (int i = 0; i < numTerminatedActors; i++) {
            terminatedActors.add(count);
            count++;
        }

        System.out.println("Actor " + numTerminatedActors + " inactive.");
    }

    public boolean hasTerminated() {
        return terminatedActors.size() == count;
    }
}
