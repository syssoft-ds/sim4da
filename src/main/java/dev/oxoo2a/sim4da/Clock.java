package dev.oxoo2a.sim4da;

public interface Clock {

    public void increase();
    public void synchronize(Network.Message m);
    public String getTime();

}
