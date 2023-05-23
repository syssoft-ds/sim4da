package dev.oxoo2a.sim4da;

public interface Clock {
    int getTime();
    void updateClock(int otherCLock);
    void increment();
    int getValue();
    void setValue(int value);

}
