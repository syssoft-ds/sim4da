package dev.oxoo2a.sim4da.clock;

public interface LogicClock {


    public void tick();
    public void synchronize(LogicClock clock, int nodeId);
    public int getTime();

}
