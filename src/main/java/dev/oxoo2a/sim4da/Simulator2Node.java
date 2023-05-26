package dev.oxoo2a.sim4da;

import java.lang.reflect.InvocationTargetException;

public interface Simulator2Node {
    void setSimulator(Node2Simulator s );

    void createClockByClass(String type, int n_nodes, int index) throws IllegalArgumentException;

    void start ();
    void stop ();
}
