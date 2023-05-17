package dev.oxoo2a.sim4da.clock;

import java.util.HashMap;


public class VectorClock extends LogicClock{
    private final HashMap<Integer, Integer> timeVector;

    public VectorClock (int nodeId){
        super(nodeId, ClockType.VECTOR);
        this.timeVector = new HashMap<>();
        this.timeVector.put(nodeId, 0);
    }

    @Override
    public void tick() {
        this.timeVector.replace(this.nodeId, this.timeVector.get(this.nodeId) + 1);
    }

    @Override
    public void synchronize(String timeStamp) {
        super.synchronize(timeStamp);
        for(Integer id: this.tempTimestamps.keySet()){

            System.out.println("Received Vector= id" + id + " : time:" + this.tempTimestamps.get(id));

            if(!this.timeVector.containsKey(id)){
                System.out.println("Vector entry was not present in current node and was added.");
                this.timeVector.put(id, this.tempTimestamps.get(id));
            }else{
                System.out.println("Current Node Vector Entry= id:" + id + " : time:" + this.timeVector.get(id) );
                this.timeVector.put(id, Math.max(this.timeVector.get(id), this.tempTimestamps.get(id)));
            }
            System.out.println("updated node time vector= id " + id + " : time:" + timeVector.get(id));
        }
        this.time = this.timeVector.get(getNodeId());
    }

    @Override
    public ClockType getType() {
        return this.type;
    }
    @Override
    public int getTime() {
        return this.timeVector.get(nodeId);
    }
    public int getTime(int id){
        return this.timeVector.get(id);
    }

    public HashMap<Integer, Integer> getTimeVector() {
        return this.timeVector;
    }

    public int getNodeId() {
        return this.nodeId;
    }
}
