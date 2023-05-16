package dev.oxoo2a.sim4da.clock;

import dev.oxoo2a.sim4da.Network;

import java.util.ArrayList;
import java.util.HashMap;


public class VectorClock implements LogicClock{
    private int nodeId;
    private HashMap<Integer, Integer> timeVector;
    public final ClockType type = ClockType.VECTOR;

    public VectorClock (int nodeId){
        this.nodeId = nodeId;

        this.timeVector = new HashMap<>();
        this.timeVector.put(nodeId, 0);
    }

    @Override
    public void tick() {
        this.timeVector.replace(this.nodeId, this.timeVector.get(this.nodeId) + 1);
    }

    @Override
    public void synchronize(String timeStamp) {

    }

    @Override
    public ClockType getType() {
        return type;
    }
    @Override
    public int getTime() {
        return timeVector.get(nodeId);
    }
    public int getTime(int id){
        return timeVector.get(id);
    }

    public HashMap<Integer, Integer> getTimeVector() {
        return timeVector;
    }

    public int getNodeId() {
        return nodeId;
    }
}
