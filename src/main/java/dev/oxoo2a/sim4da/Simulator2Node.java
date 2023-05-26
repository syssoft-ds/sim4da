package dev.oxoo2a.sim4da;

public interface Simulator2Node {
    void setSimulator(Node2Simulator s );

    void setClock(Clock clock);

    void start ();
    void stop ();
}
