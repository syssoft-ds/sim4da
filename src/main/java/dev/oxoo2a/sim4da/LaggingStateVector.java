package dev.oxoo2a.sim4da;

import java.util.List;

public class LaggingStateVector {
    private int[] vectorClock;
    private int numActors;

    public LaggingStateVector(int numActors) {
        this.numActors = numActors;
        vectorClock = new int[numActors];
    }

    public void receiveMessage(List<Actor> actors, int senderId, int[] senderVectorClock) {
        for (int i = 0; i < numActors; i++) {
            vectorClock[i] = Math.max(vectorClock[i], senderVectorClock[i]);
        }
        vectorClock[senderId]++;

        // Überprüfe auf Terminierung anhand des nachlaufenden Kontrollvektors
        for (int i = 0; i < numActors; i++) {
            if (i != senderId && vectorClock[i] == vectorClock[senderId] - 1) {
                vectorClock[i]++;
            }
        }
    }

    public boolean hasTerminated() {
        for (int i = 0; i < numActors; i++) {
            if (vectorClock[i] == 0)
                return false;
        }
        return true;
    }
}
