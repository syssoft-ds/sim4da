package dev.oxoo2a.sim4da;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import dev.oxoo2a.sim4da.Message.MessageType;

public abstract class Node implements Runnable {
    
    protected final int id;
    private final MessageQueue messageQueue = new MessageQueue();
    private final Simulator simulator;
    private final Thread thread = new Thread(this);
    private final Random random = new Random();
    
    public Node(Simulator simulator, int id) {
        this.simulator = simulator;
        this.id = id;
    }
    
    public void start() {
        thread.start();
    }
    
    public void stop() {
        thread.interrupt(); // Stop waiting in receive
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
    
    protected int getNumberOfNodes() {
        return simulator.getNumberOfNodes();
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
            String messageTypeString = m.getType()==MessageType.BROADCAST ? "Broadcast" : "Unicast";
            simulator.emitToTracer("Receive %s:%d<-%d", messageTypeString, m.getReceiverId(), m.getSenderId());
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
        // To avoid reading all messages in FIFO order (which is not guaranteed in a real network)
        // we order them according to a random but fixed priority.
        // All BlockingQueue implementations are thread-safe, so no external synchronization is required.
        private final PriorityBlockingQueue<MessageWithPriority> queue = new PriorityBlockingQueue<>();
        private void put(Message message) {
            queue.put(new MessageWithPriority(message));
        }
        private Message await() {
            try {
                return queue.take().message;
            } catch (InterruptedException ignored) {}
            return null; // Simulation time ended before a message was received
        }
        private class MessageWithPriority implements Comparable<MessageWithPriority> {
            private final Message message;
            private final int priority;
            private MessageWithPriority(Message message) {
                this.message = message;
                this.priority = random.nextInt(100); //limit range to avoid int overflows when comparing
            }
            @Override
            public int compareTo(MessageWithPriority other) {
                return priority-other.priority;
            }
        }
    }
}
