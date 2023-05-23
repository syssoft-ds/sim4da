package dev.oxoo2a.sim4da;

import java.util.Arrays;

public class VectorClock implements Clock {
    private int[] values;
    private int nodeId;

    public VectorClock(int size, int nodeID) {
        this.values = new int[size];
        Arrays.fill(this.values, 0);
        this.nodeId = nodeID;
    }

    @Override
    public void updateClock(int receivedTimestamp) {
        this.values[nodeId] = Math.max(this.values[nodeId], receivedTimestamp) + 1;
    }

    @Override
    public void increment() {
        int nodeId = getNodeID();
        this.values[nodeId]++;
    }

    @Override
    public int getTime() {
        return this.values[getNodeID()];
    }
    @Override
    public int getValue() {
        return this.values[getNodeID()];
    }

    @Override
    public void setValue(int value) {
        int nodeId = getNodeID();
        this.values[nodeId] = value;
    }

    private int getNodeID() {
        return this.nodeId;
    }

    public int[] getValues() {
        return this.values;
    }
}
