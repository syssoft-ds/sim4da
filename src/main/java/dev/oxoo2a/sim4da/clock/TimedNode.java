package dev.oxoo2a.sim4da.clock;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;


/**
 * Basic Parent class for implementing Nodes with Logic Clocks.
 * Time keeping is handled in send and receive methods using an Instance of LogicClock for each Node.
 * Sending includes adding the nodes timestamp according to the used ClockType.
 * Receiving includes parsing the time Information and updating a node's timestamp (synchronize(), see LogicClock.java)
 */
public class TimedNode extends Node {
    LogicClock lc;

    public TimedNode(int my_id, ClockType type) {
        super(my_id);
        this.lc = ClockUtil.create(my_id, type);
    }

    protected void sendUnicast ( int receiver_id, Message m ) {

        this.lc.tick();
        emit("%d ticked to %d",this.myId ,this.lc.getTime());


        // clear all entries from message containing the timestamp identifier '%T'.
        m.getMap().entrySet().removeIf(entry -> entry.getKey().contains("%T"));

        if(this.lc instanceof LampertClock){
            m.add("%T"+ myId, lc.getTime());
        }
        if(this.lc instanceof VectorClock){
            for (Integer id: ((VectorClock) lc).getTimeVector().keySet()){
                m.add("%T"+id, ((VectorClock) lc).getTimeVector().get(id));
            }
        }
        if(this.lc instanceof VectorClock){
            ((VectorClock) this.lc).printVectorLine(((VectorClock) this.lc).getTimeVector());
        }
        System.out.println();

        this.simulator.sendUnicast(myId,receiver_id, m.toJson());
    }

    protected Network.Message receive () {

        Network.Message m = simulator.receive(myId);

        lc.tick();
        emit("%d ticked to %d",this.myId ,this.lc.getTime());

        // synchronisation of clocks
        if(m != null){
            this.lc.synchronize(m.payload);
        }


        return m;
    }


    public LogicClock getLc() {
        return lc;
    }

    @Override
    protected void main() {}



}
