package dev.oxoo2a.sim4da.clock;

import dev.oxoo2a.sim4da.Simulator;

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
        System.out.print("Node " +this.getNodeId() + " is synchronizing " );
        this.printVectorLine(this.getTimeVector());
        System.out.print("(own) and ");
        this.printVectorLine(this.tempTimestamps);

        for(Integer id: this.tempTimestamps.keySet()){
            if(!this.timeVector.containsKey(id)){
                this.timeVector.put(id, this.tempTimestamps.get(id));
            }else{
                this.timeVector.put(id, Math.max(this.timeVector.get(id), this.tempTimestamps.get(id)));
            }
        }

        System.out.println();

        this.printTimeStamps();
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

    @Override
    protected void printTimeStamps(){
        System.out.print("CurrentVector of Node " + this.nodeId + " | Time  = ");
        printVectorLine(this.timeVector);
        System.out.println();
    }


    protected void printVectorLine(HashMap<Integer,Integer> vector){
        System.out.print("[");
        int counter = 0;
        int max = vector.size();
        for (Integer k : vector.keySet()){
            System.out.print(k +  ":" + vector.get(k));
            if(!(vector.size()== ++counter)){
                System.out.print(",");
            }
        }
        System.out.print("] ");
    }


    public int getNodeId() {
        return this.nodeId;
    }
}
