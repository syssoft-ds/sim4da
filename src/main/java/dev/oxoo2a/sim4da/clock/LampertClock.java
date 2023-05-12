package dev.oxoo2a.sim4da.clock;

public class LampertClock implements LogicClock{
    private int nodeId;
    private int time;
    public final ClockType type = ClockType.LAMPORT;

    public LampertClock(int nodeId){
        this.nodeId = nodeId;
        this.time = 0;
    }


    @Override
    public void tick() {
        this.time++;

    }

    @Override
    public void synchronize(LogicClock clock, int nodeId) {
        this.time = Math.max(((LampertClock) clock).getTime(), this.time);

    }

    public int getTime() {
        return time;
    }

    public int getNodeId() {
        return nodeId;
    }
}
