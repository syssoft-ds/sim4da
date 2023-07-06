package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Node;

import java.util.HashMap;
import java.util.Map;

public class ControlVectorCoordinator extends Node {

    public static Map<Integer, Integer[]> clientVectors = new HashMap<>();

    public ControlVectorCoordinator(int my_id) {

        super(my_id);
    }

    @Override
    protected void main() {

    }


}
