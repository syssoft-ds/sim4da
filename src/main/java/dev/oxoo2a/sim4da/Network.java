package dev.oxoo2a.sim4da;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import dev.oxoo2a.sim4da.Message.MessageType;

public class Network {
    
    private static final Random RANDOM = new Random();
    
    private final int numberOfNodes;
    private final Tracer tracer;
    private final MessageQueue[] messageQueues;
    
    //private static Logger logger = Logger.getRootLogger();
    
    public Network(int numberOfNodes, Tracer tracer) {
        this.numberOfNodes = numberOfNodes;
        this.tracer = tracer;
        messageQueues = new MessageQueue[numberOfNodes];
        for (int i = 0; i<numberOfNodes; i++)
            messageQueues[i] = new MessageQueue();
    }
    
    public int getNumberOfNodes() {
        return numberOfNodes;
    }
    
    public void unicast(int senderId, int receiverId, String message) {
        if (receiverId<0 || receiverId>=numberOfNodes) {
            System.err.printf("Network::unicast: unknown receiver id %d\n", receiverId);
            return;
        }
        if (senderId<0 || senderId>=numberOfNodes) {
            System.err.printf("Network::unicast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Unicast:%d->%d", senderId, receiverId);
        Message raw = new Message(senderId, receiverId, MessageType.UNICAST, message);
        messageQueues[receiverId].put(raw);
    }
    
    public void broadcast(int senderId, String message) {
        if (senderId<0 || senderId>=numberOfNodes) {
            System.err.printf("Network::broadcast: unknown sender id %d\n", senderId);
            return;
        }
        tracer.emit("Broadcast:%d->0..%d", senderId, numberOfNodes-1);
        Message raw = new Message(senderId, -1, MessageType.BROADCAST, message);
        for (int l = 0; l<numberOfNodes; l++) {
            if (l==senderId) continue;
            raw.receiverId = l;
            messageQueues[l].put(raw);
        }
    }
    
    public Message receive(int receiverId) {
        if (receiverId<0 || receiverId>=numberOfNodes) {
            System.err.printf("Network::receive: unknown receiver id %d\n", receiverId);
            return null;
        }
        Message m = messageQueues[receiverId].await();
        if (m!=null) {
            String m_type = m.type==MessageType.BROADCAST ? "Broadcast" : "Unicast";
            tracer.emit("Receive %s:%d<-%d", m_type, m.receiverId, m.senderId);
        }
        return m;
    }
    
    public void stop() {
        for (MessageQueue queue : messageQueues)
            queue.stop();
    }
    
    private static class MessageQueue {
        private final LinkedList<Message> queue = new LinkedList<>();
        private final Semaphore awaitMessage = new Semaphore(0);
        private boolean stop = false;
        public void put(Message message) {
            synchronized (queue) {
                queue.addLast(message);
                awaitMessage.release();
            }
        }
        public Message await() {
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
