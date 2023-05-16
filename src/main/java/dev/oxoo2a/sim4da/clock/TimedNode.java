package dev.oxoo2a.sim4da.clock;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;
import dev.oxoo2a.sim4da.Node2Simulator;

public class TimedNode extends Node {
    LogicClock lc;

    public TimedNode(int my_id, ClockType type) {
        super(my_id);
        this.lc = ClockUtil.create(my_id, type);

    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        System.out.println("Sending timed unicast message");
        this.lc.tick();
        if(this.lc instanceof LampertClock){
            m.add("%T"+ myId, lc.getTime());
        }
        if(this.lc instanceof VectorClock){
            for (Integer id: ((VectorClock) lc).getTimeVector().keySet()){
                m.add("%T"+id, ((VectorClock) lc).getTime(id));
            }
        }
        System.out.println(m);
        this.simulator.sendUnicast(myId,receiver_id, m.toJson());
    }
    protected Network.Message receive () {
        Network.Message m = simulator.receive(myId);
        System.out.println("Network.Message received by node " + myId);
        System.out.println(m);
        System.out.println("payload");
        System.out.println(m.payload.getClass());
        return m;
    }

    @Override
    protected void main() {

    }



}
