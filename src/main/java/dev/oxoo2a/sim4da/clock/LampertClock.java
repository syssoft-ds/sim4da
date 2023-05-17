package dev.oxoo2a.sim4da.clock;

public class LampertClock extends LogicClock{

    public LampertClock(int nodeId){
        super(nodeId, ClockType.LAMPORT);
        this.time = 0;
    }

    @Override
    public void synchronize(String timeStamp) {
        super.synchronize(timeStamp);
        if(this.tempTimestamps.keySet().size()>1){
            System.err.println("more than one timestamp was extracted from string, should not happen with lamportClock");
            System.out.println(timeStamp);
        }
        for (Integer senderId : tempTimestamps.keySet()){
            this.time = Math.max(this.time, tempTimestamps.get(senderId));
        }
    }



    @Override
    public void tick() {
        this.time++;
    }

    @Override
    public ClockType getType() {
        return type;
    }


    public int getTime() {
        return time;
    }

    public int getNodeId() {
        return this.getNodeId();
    }
}
