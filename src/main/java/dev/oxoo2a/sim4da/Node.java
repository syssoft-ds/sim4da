package dev.oxoo2a.sim4da;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import dev.oxoo2a.sim4da.Message.MessageType;

public abstract class Node {
    
    private static final Random RANDOM = new Random();
    
    protected final int id;
    private final MessageQueue messageQueue = new MessageQueue();
    private Simulator simulator;
    private Tracer tracer;
    private final Thread thread = new Thread(this::run);
    private boolean stop = false;
    
    public Node(int id) {
        this.id = id;
    }
    
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
    
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public void start() {
        thread.start();
    }
    
    public void stop() {
        stop = true;
        messageQueue.stop(); // Stop waiting in receive
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
    
    protected int getNumberOfNodes() {
        return simulator.getNumberOfNodes();
    }
    
    protected boolean stillSimulating() {
        return !stop;
    }
    
    protected void sendUnicast(int receiverId, String messageContent) {
        simulator.unicast(id, receiverId, messageContent);
    }
    
    protected void sendUnicast(int receiverId, JsonSerializableMap messageContent) {
        simulator.unicast(id, receiverId, messageContent.toJson());
    }
    
    protected void sendBroadcast(String messageContent) {
        simulator.broadcast(id, messageContent);
    }
    
    protected void sendBroadcast(JsonSerializableMap messageContent) {
        simulator.broadcast(id, messageContent.toJson());
    }
    
    protected Message receive() {
        Message m = messageQueue.await();
        if (m!=null) {
            String messageTypeString = m.type==MessageType.BROADCAST ? "Broadcast" : "Unicast";
            tracer.emit("Receive %s:%d<-%d", messageTypeString, m.receiverId, m.senderId);
        }
        return m;
    }
    
    // package-private because this shouldn't be used by application code
    void putInMessageQueue(Message message) {
        messageQueue.put(message);
    }
    
    // Module implements basic node functionality
    protected abstract void run();
    
    private static class MessageQueue {
        private final LinkedList<Message> queue = new LinkedList<>();
        private final Semaphore awaitMessage = new Semaphore(0);
        private boolean stop = false;
        private void put(Message message) {
            synchronized (queue) {
                queue.addLast(message);
                awaitMessage.release();
            }
        }
        private Message await() {
            while (!stop) {
                try {
                    awaitMessage.acquire();
                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            // Return a random message in queue avoiding FIFO order
                            if (queue.size() == 1)
                                return queue.removeFirst();
                            int c = RANDOM.nextInt(queue.size());
                            return queue.remove(c);
                        }
                    }
                } catch (InterruptedException ignored) {}
            }
            return null; // Simulation time ended before a message was received
        }
        public void stop() {
            stop = true;
            awaitMessage.release();
        }
    }
}
