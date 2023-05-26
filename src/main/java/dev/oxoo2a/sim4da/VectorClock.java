package dev.oxoo2a.sim4da;

import java.util.Arrays;
import java.util.List;

public class VectorClock {
    private int[] clocks;

    public VectorClock(int n_nodes) {
        clocks = new int[n_nodes];
    }

    public synchronized void updateClock(int nodeId) {
        clocks[nodeId] = clocks[nodeId] + 1;
    }

    public synchronized List<int[]> getClock() {
        return Arrays.asList(clocks);
    }
}
