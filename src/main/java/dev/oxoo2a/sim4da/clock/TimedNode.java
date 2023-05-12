package dev.oxoo2a.sim4da.clock;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Node2Simulator;

public class TimedNode extends Node {
    LogicClock lc;

    public TimedNode(int my_id, ClockType type) {
        super(my_id);
        this.lc = ClockFactory.create(my_id, type);

    }

    protected void sendUnicast ( int receiver_id, Message m ) {

        lc.tick();
        if(this.lc instanceof LampertClock){
            m.add("LC."+ myId, lc.getTime());
        }
        if(this.lc instanceof VectorClock){
            for (Integer id: ((VectorClock) lc).getTimeVector().keySet()){
                m.add("LC."+id, ((VectorClock) lc).getTime(id));
            }
        }
        simulator.sendUnicast(myId,receiver_id, m.toJson());
    }

    @Override
    protected void main() {

    }


    private Node2Simulator simulator;

}
