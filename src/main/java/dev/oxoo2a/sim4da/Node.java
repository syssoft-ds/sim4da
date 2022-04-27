package dev.oxoo2a.sim4da;

public abstract class Node {

    public Node ( int my_id ) {
        this.myId = my_id;
        stop = false;
        t_main = new Thread(this::main);
    }

    public void setNetwork ( Network network ) {
        this.network = network;
    }
    public void start () {
        t_main.start();
    }
    private void sleep ( long millis ) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {};
    }

    protected int numberOfNodes() { return network.numberOfNodes(); };

    protected boolean stillSimulating () {
        return !stop;
    }
    protected void sendUnicast ( int receiver_id, String m ) {
        network.unicast(myId,receiver_id,m);
    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        network.unicast(myId,receiver_id, m.toJson());
    }

    protected void sendBroadcast ( String m ) {
        network.broadcast(myId,m);
    }

    protected void sendBroadcast ( Message m ) {
        network.broadcast(myId,m.toJson());
    }

    protected Network.Message receive () {
        return network.receive(myId);
    }

    // Module implements basic node functionality
    protected abstract void main ();

    public void stop () {
        stop = true;
        try {
            t_main.join();
        }
        catch (InterruptedException ignored) {};
    }

    protected final int myId;
    private Network network;
    private final Thread t_main;
    private boolean stop;
}
