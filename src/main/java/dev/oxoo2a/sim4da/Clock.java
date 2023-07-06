package dev.oxoo2a.sim4da;

public interface Clock {
    int getTimeStamp();
    void increment();
    public String printTimeStamp();
    public void update(int senderTime,  Object ... args);
    String getTimeVector();
}




