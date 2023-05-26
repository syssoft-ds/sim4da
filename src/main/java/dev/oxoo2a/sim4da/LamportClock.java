package dev.oxoo2a.sim4da;

public class LamportClock implements Clock {
    private int value; //current value of Lamport Clock

    public LamportClock() {
        this.value = 0; //initializing with initial value zero
    }

    //called when receiving a message from another process. It takes the value of the other process's clock and updates the clock's value
    @Override
    public void updateClock(int otherValue) {
        this.value = Math.max(this.value, otherValue) + 1; //clock is incremented to the maximum value between its current value and the received value, plus 1.
    }

    @Override
    public int getTime() {
        return this.value;
    } //returns current clock value
    @Override
    public void increment() {
        this.value++;
    } //increments clock value by 1

    @Override
    public int getValue() {
        return this.value;
    } //returns current clock value

    @Override
    public void setValue(int value) {
        this.value = value;
    } //set value of clock
}
