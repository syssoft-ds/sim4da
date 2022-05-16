package dev.oxoo2a.sim4da;

public abstract class Node {
    
    protected final int myId;
    private Network network;
    private Tracer tracer;
    private final Thread thread = new Thread(this::main);
    private boolean stop = false;
    
    public Node(int myId) {
        this.myId = myId;
    }
    
    public void setNetwork(Network network) {
        this.network = network;
    }
    
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public void start() {
        thread.start();
    }
    
    public void stop () {
        stop = true;
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
    
    protected int getNumberOfNodes() {
        return network.getNumberOfNodes();
    }
    
    protected boolean stillSimulating() {
        return !stop;
    }
    
    protected void sendUnicast(int receiverId, String m) {
        network.unicast(myId, receiverId, m);
    }
    
    protected void sendUnicast(int receiverId, Message m) {
        network.unicast(myId, receiverId, m.toJson());
    }
    
    protected void sendBroadcast(String m) {
        network.broadcast(myId, m);
    }
    
    protected void sendBroadcast(Message message) {
        network.broadcast(myId, message.toJson());
    }
    
    protected Network.Message receive() {
        return network.receive(myId);
    }
    
    // Module implements basic node functionality
    protected abstract void main();
}
