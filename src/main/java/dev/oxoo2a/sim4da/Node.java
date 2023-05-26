package dev.oxoo2a.sim4da;

public abstract class Node implements Simulator2Node {

    public Node ( int my_id, VectorClock clock, Tracer tracer) {
        this.id = my_id;
        this.myId = my_id;
        this.clock = clock;
        t_main = new Thread(this::main);
    }



    public int getId() {
        return id;
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

    protected void sendUnicast ( int receiver_id, Message m, int timestamp ) {
        m.setTimestamp(timestamp);
        simulator.sendUnicast(myId,receiver_id, m.toJson());
        clock.increment();
    }

    protected void sendBroadcast ( String m ) {
        simulator.sendBroadcast(myId,m);
    }

    protected void sendBroadcast ( Message m ) {
        simulator.sendBroadcast(myId,m.toJson());
    }

    protected Network.Message receive () {
        Network.Message m = simulator.receive(myId);

        if (m != null) {
            int timestamp = m.timestamp;
            clock.updateClock(timestamp);
        }
        return m;
    }

    protected void emit ( String format, Object ... args ) {
        simulator.emit(format,args);
    }
    // Module implements basic node functionality
    protected abstract void main ();
    public void setClock(VectorClock clock) {
        this.clock = clock;
    }

    @Override
    public void stop () {
        try {
            t_main.join();
        }
        catch (InterruptedException ignored) {};
    }
    protected Clock clock;
    protected final int myId;
    private Node2Simulator simulator;
    private final Thread t_main;
    private int id;
}
