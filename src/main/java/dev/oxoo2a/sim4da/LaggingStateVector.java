package dev.oxoo2a.sim4da;

import java.util.Arrays;
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

        // Check termination based on the lagging state vector
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

    public int[] getVectorClock() {
        return vectorClock.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(vectorClock);
    }
}
