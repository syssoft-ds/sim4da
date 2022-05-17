package dev.oxoo2a.sim4da;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public abstract class Node {
    
    private static final Random RANDOM = new Random();
    
    protected final int id;
    private final MessageQueue messageQueue = new MessageQueue();
    private Network network;
    private Tracer tracer;
    private final Thread thread = new Thread(this::run);
    private boolean stop = false;
    
    public Node(int id) {
        this.id = id;
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
        messageQueue.stop(); // Stop waiting in receive
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
    
    protected int getNumberOfNodes() {
        return network.getNumberOfNodes();
    }
    
    protected boolean stillSimulating() {
        return !stop;
    }
    
    protected void sendUnicast(int receiverId, String messageContent) {
        network.unicast(id, receiverId, messageContent);
    }
    
    protected void sendUnicast(int receiverId, JsonSerializableMap messageContent) {
        network.unicast(id, receiverId, messageContent.toJson());
    }
    
    protected void sendBroadcast(String messageContent) {
        network.broadcast(id, messageContent);
    }
    
    protected void sendBroadcast(JsonSerializableMap messageContent) {
        network.broadcast(id, messageContent.toJson());
    }
    
    protected Message receive() {
        return network.receive(id);
    }
    
    // The two methods below are package-private because they shouldn't be used by application code
    
    void putInMessageQueue(Message message) {
        messageQueue.put(message);
    }
    
    Message awaitFromMessageQueue() {
        return messageQueue.await();
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
