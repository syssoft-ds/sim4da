package dev.oxoo2a.sim4da;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import dev.oxoo2a.sim4da.Message.MessageType;

public abstract class Node implements Runnable {
    
    protected final int id;
    private final MessageQueue messageQueue = new MessageQueue();
    private final Simulator simulator;
    private final Thread thread = new Thread(this);
    
    public Node(Simulator simulator, int id) {
        this.simulator = simulator;
        this.id = id;
    }
    
    public void start() {
        thread.start();
    }
    
    public void stop() {
        messageQueue.stop(); // Stop waiting in receive
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
    
    protected int getNumberOfNodes() {
        return simulator.getNumberOfNodes();
    }
    
    protected Random getRandom() {
        return simulator.getRandom();
    }
    
    protected boolean isStillSimulating() {
        return simulator.isStillSimulating();
    }
    
    protected void sendUnicast(int receiverId, String messageContent) {
        simulator.sendUnicast(id, receiverId, messageContent);
    }
    
    protected void sendUnicast(int receiverId, JsonSerializableMap messageContent) {
        simulator.sendUnicast(id, receiverId, messageContent.toJson());
    }
    
    protected void sendBroadcast(String messageContent) {
        simulator.sendBroadcast(id, messageContent);
    }
    
    protected void sendBroadcast(JsonSerializableMap messageContent) {
        simulator.sendBroadcast(id, messageContent.toJson());
    }
    
    protected Message receive() {
        Message m = messageQueue.await();
        if (m!=null) {
            String messageTypeString = m.type==MessageType.BROADCAST ? "Broadcast" : "Unicast";
            simulator.emitToTracer("Receive %s:%d<-%d", messageTypeString, m.receiverId, m.senderId);
        }
        return m;
    }
    
    // package-private because this shouldn't be used by application code
    void putInMessageQueue(Message message) {
        messageQueue.put(message);
    }
    
    // Class implements only basic node functionality
    @Override
    public abstract void run();
    
    private class MessageQueue {
        private final LinkedList<Message> queue = new LinkedList<>();
        private final Semaphore awaitMessage = new Semaphore(0);
        private void put(Message message) {
            synchronized (queue) {
                queue.addLast(message);
                awaitMessage.release();
            }
        }
        private Message await() {
            while (isStillSimulating()) {
                try {
                    awaitMessage.acquire();
                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            // Return a random message in queue avoiding FIFO order
                            if (queue.size()==1) return queue.removeFirst();
                            int c = getRandom().nextInt(queue.size());
                            return queue.remove(c);
                        }
                    }
                } catch (InterruptedException ignored) {}
            }
            return null; // Simulation time ended before a message was received
        }
        private void stop() {
            awaitMessage.release();
        }
    }
}
