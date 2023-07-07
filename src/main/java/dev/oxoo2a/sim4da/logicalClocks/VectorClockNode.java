package dev.oxoo2a.sim4da.logicalClocks;

public class VectorClockNode extends LogicalClockNode{

    public VectorClockNode(int my_id, int knowledgeOfTheTotalAmountOfNodes) {
        super(my_id, VectorClockNode.class, knowledgeOfTheTotalAmountOfNodes);
    }

}
