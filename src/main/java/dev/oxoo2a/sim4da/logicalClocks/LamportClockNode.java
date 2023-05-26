package dev.oxoo2a.sim4da.logicalClocks;

public class LamportClockNode extends LogicalClockNode{

    public LamportClockNode(int my_id) {
        super(my_id, LamportClockNode.class, 0);
    }

}
