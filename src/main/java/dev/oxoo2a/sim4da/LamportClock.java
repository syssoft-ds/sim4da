package dev.oxoo2a.sim4da;

public class LamportClock implements Clock {
    private int value;

    public LamportClock() {
        this.value = 0;
    }

    @Override
    public void updateClock(int otherValue) {
        this.value = Math.max(this.value, otherValue) + 1;
    }

    @Override
    public int getTime() {
        return this.value;
    }
    @Override
    public void increment() {
        this.value++;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
}
