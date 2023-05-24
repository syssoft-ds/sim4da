package dev.oxoo2a.sim4da;

public abstract class Node implements Simulator2Node {

    enum ClockType{ LAMPORT, VECTOR}

    public Node ( int my_id ) {
        clock = new LamportClock();
        this.myId = my_id;
        t_main = new Thread(this::main);
    }

    public Node ( int my_id, ClockType clockType ) {
        if (clockType == ClockType.VECTOR) {
            clock = null;
        } else {
            clock = new LamportClock();
        }
        this.myId = my_id;
        t_main = new Thread(this::main);
    }

    @Override
    public void setSimulator(Node2Simulator s ) {
        this.simulator = s;
    }

    @Override
    public void start () {
        t_main.start();
    }

    private void sleep ( long millis ) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {};
    }

    protected int numberOfNodes() { return simulator.numberOfNodes(); };

    protected boolean stillSimulating () {
        return simulator.stillSimulating();
    }
    protected void sendUnicast ( int receiver_id, String m ) {
        simulator.sendUnicast(myId,receiver_id,m);
    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        clock.increase();
        m.add("Time", clock.getTime());
        simulator.sendUnicast(myId,receiver_id, m.toJson());
    }

    protected void sendBroadcast ( String m ) {
        simulator.sendBroadcast(myId,m);
    }

    protected void sendBroadcast ( Message m ) {
        if (clock != null)
            clock.increase();
        simulator.sendBroadcast(myId,m.toJson());
    }

    protected Network.Message receive () {
        Network.Message m = simulator.receive(myId);
        if (m != null) {
            String timeBefore = clock.getTime();
            clock.synchronize(m);
            String timeAfter = clock.getTime();
            emit("Node %d: received - before: %s -> after: %s.", myId, timeBefore, timeAfter);
        }
        return m;
    }

    protected void emit ( String format, Object ... args ) {
        simulator.emit(format, args);
    }


    // Module implements basic node functionality
    protected abstract void main ();

    @Override
    public void stop () {
        try {
            t_main.join();
        }
        catch (InterruptedException ignored) {};
    }

    protected final int myId;
    public Clock clock;
    private Node2Simulator simulator;
    private final Thread t_main;
}
