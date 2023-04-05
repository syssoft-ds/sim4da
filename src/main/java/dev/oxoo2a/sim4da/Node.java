package dev.oxoo2a.sim4da;

public abstract class Node {

    public Node ( int my_id ) {
        this.myId = my_id;
        t_main = new Thread(this::main);
    }

    public void setSimulation ( Simulation s ) {
        this.simulation = s;
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

    protected int numberOfNodes() { return simulation.numberOfNodes(); };

    protected boolean stillSimulating () {
        return simulation.stillSimulating();
    }
    protected void sendUnicast ( int receiver_id, String m ) {
        simulation.sendUnicast(myId,receiver_id,m);
    }

    protected void sendUnicast ( int receiver_id, Message m ) {
        simulation.sendUnicast(myId,receiver_id, m.toJson());
    }

    protected void sendBroadcast ( String m ) {
        simulation.sendBroadcast(myId,m);
    }

    protected void sendBroadcast ( Message m ) {
        simulation.sendBroadcast(myId,m.toJson());
    }

    protected Network.Message receive () {
        return simulation.receive(myId);
    }

    // Module implements basic node functionality
    protected abstract void main ();

    public void stop () {
        try {
            t_main.join();
        }
        catch (InterruptedException ignored) {};
    }

    protected final int myId;
    private Simulation simulation;
    private final Thread t_main;
}
