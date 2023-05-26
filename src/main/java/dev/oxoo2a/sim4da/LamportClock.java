package dev.oxoo2a.sim4da;

public class LamportClock {
    private int[] clocks;

    public LamportClock(int n_nodes) {
        clocks = new int[n_nodes];
    }

    public synchronized void combineClock(int receiverId, int senderId) {
        clocks[receiverId] = Math.max(clocks[receiverId], senderId) + 1;
    }
    public synchronized void updateClock(int nodeId) {
        clocks[nodeId] = clocks[nodeId] + 1;
    }

    public synchronized int getClock(int nodeId) {
        return clocks[nodeId];
    }
}
