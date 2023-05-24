package dev.oxoo2a.sim4da;

public interface Clock {
    int getTime(); //returns the current time or value of the clock
    void updateClock(int otherCLock); //updates the clock based on the value of another clock.
    void increment(); // increments the clock value
    int getValue(); //returns the current value of the clock
    void setValue(int value); //sets the value of the clock to a specific value

}
