package dev.oxoo2a.sim4da;

public class Lamport implements Clock{

    public Lamport()
    {
        time = 0;
    }
    @Override
    public int getTimeStamp() {
        return time;
    }

    @Override
    public void increment() {
        time++;
    }

    @Override
    public String printTimeStamp()
    {
        return  String.valueOf(time);
    }

    public void update(int senderTime, Object... args)
    {
        time = Math.max(time, senderTime) + 1;
    }

    @Override
    public String getTimeVector() {
        return null;
    }

    private int time;
}
