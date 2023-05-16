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
            m.getMap().entrySet().removeIf(entry -> entry.getKey().contains("%T"));
            m.add("%T"+ myId, lc.getTime());
            System.out.println("Added [" + myId + "] : [" + lc.getTime()+ "] to message payload");
        }
        if(this.lc instanceof VectorClock){
            m.getMap().entrySet().removeIf(entry -> entry.getKey().contains("%T"));
            for (Integer id: ((VectorClock) lc).getTimeVector().keySet()){
                m.add("%T"+id, ((VectorClock) lc).getTimeVector().get(id));
            }
        }
        System.out.println(m);
        this.simulator.sendUnicast(myId,receiver_id, m.toJson());
    }
    protected Network.Message receive () {
        lc.tick();

        Network.Message m = simulator.receive(myId);
        if(m != null){
            this.lc.synchronize(m.payload);
        }

        return m;
    }

    @Override
    protected void main() {

    }



}
