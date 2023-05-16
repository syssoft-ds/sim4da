package dev.oxoo2a.sim4da;
import java.lang.IllegalArgumentException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class Node implements Simulator2Node {

    public Node (int my_id ) {
        this.myId = my_id;
        t_main = new Thread(this::main);
    }

    @Override
    public void setSimulator(Node2Simulator s ) {
        this.simulator = s;
    }
    @Override
    public void createClockByClass(String type, int n_nodes, int index) throws IllegalArgumentException {

        switch (type) {
            case "lamport":
                this.clock = new Lamport();
                break;
            case "vector":
                this.clock = new Vector(n_nodes, index);
                break;
            default:
                throw new IllegalArgumentException("The class name must be either vector or lamport!");
        }


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
        simulator.sendUnicast(myId,receiver_id, m.toJson());
    }

    protected void sendBroadcast ( String m ) {
        simulator.sendBroadcast(myId,m);
    }

    protected void sendBroadcast ( Message m ) {
        simulator.sendBroadcast(myId,m.toJson());
    }

    protected Network.Message receive () {
        return simulator.receive(myId);
    }

    protected void emit (String format, String logType,  Object ... args ) {
        simulator.emit(format,logType,args);
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
    protected Node2Simulator simulator;
    private final Thread t_main;
    protected Clock clock;
}
