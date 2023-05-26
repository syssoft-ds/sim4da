package dev.oxoo2a.sim4da;

import java.util.Arrays;

public class VectorClock implements Clock {
    private int[] values; //value at each index represents logical time for that node.
    private int nodeId; //holds current node's ID. Node ID is used to update clock value of specific node.

    public VectorClock(int size, int nodeID, int initialValues) {
        this.values = new int[size]; //total number of nodes in system
        Arrays.fill(this.values, 0);  //set all elements to initialValues
        this.nodeId = nodeID; //represents current nodeID
    }

    //called when a node receives a message with a timestamp
    //It compares the received timestamp with the current node's timestamp and updates it
    @Override
    public void updateClock(int receivedTimestamp) {
        this.values[nodeId] = Math.max(this.values[nodeId], receivedTimestamp) + 1; //ensuring clock to reflect maximum observed value across all nodes
    }

    @Override
    public void increment() { //increments clock value at the index corresponding to current node's ID
        int nodeId = getNodeID();
        this.values[nodeId]++;
    }

    @Override
    public int getTime() {
        return this.values[getNodeID()];
    } //returns current node's clock value

    @Override
    public int getValue() {
        return this.values[getNodeID()]; //access to clock value of current node
    }
    @Override
    public void setValue(int value) {
        this.values[getNodeID()] = value;
    } //access to clock value of current node

    private int getNodeID() {
        return this.nodeId; //returns ID of current node
    }

    public int[] getValues() {
        return this.values;
    } //returns the entire array of clock values for all nodes
}
