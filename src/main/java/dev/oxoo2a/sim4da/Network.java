package dev.oxoo2a.sim4da;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Network {

    public Network ( int n_nodes ) {
        this.n_nodes = n_nodes;
        mqueues = new MessageQueue[n_nodes];
        for (int i=0; i<n_nodes; ++i)
            mqueues[i] = new MessageQueue();
    }

    public int numberOfNodes () {
        return n_nodes;
    }

    public enum MessageType { UNICAST, BROADCAST }

    public class Message {
        public Message(int sender_id, int receiver_id, MessageType type, String payload ) {
            this.sender_id = sender_id;
            this.receiver_id = receiver_id;
            this.type = type;
            this.payload = payload;
        }
        public int sender_id;
        public int receiver_id;
        public MessageType type;
        public String payload;

        public String toString () {
            String r = "Network::Message(sender="+sender_id+",receiver="+receiver_id+",";
            r += type == MessageType.BROADCAST ? "Broadcast" : "Unicast";
            r += ",payload=<"+payload+">)";
            return r;
        }
    }

    private class MessageQueue {
        public MessageQueue () {
            queue = new LinkedList<>();
            await_message = new Semaphore(0);
            stop = false;
        }

        public void put ( Message r ) {
            synchronized (queue) {
                queue.addLast(r);
                await_message.release();
            }
        }

        public Message await () {
            while (true) {
                if (stop) return null;
                try {
                    await_message.acquire();
                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            // Return a random message in queue avoiding FIFO order
                            if (queue.size() == 1)
                                return queue.removeFirst();
                            else {
                                int c = rgen.nextInt(queue.size());
                                return queue.remove(c);
                            }
                        }
                    }
                }
                catch (InterruptedException e) {};
            }
        }

        public void stop () {
            stop = true;
            await_message.release();
        }

        private final LinkedList<Message> queue;
        private final Semaphore await_message;
        private boolean stop;
    }

    public void unicast ( int sender_id, int receiver_id, String message ) {
        if ((receiver_id < 0) || (receiver_id >= n_nodes)) {
            System.err.printf("Network::unicast: unknown receiver id %d\n",receiver_id);
            return;
        }
        if ((sender_id < 0) || (sender_id >= n_nodes)) {
            System.err.printf("Network::unicast: unknown sender id %d\n",sender_id);
            return;
        }
        Message raw = new Message(sender_id,receiver_id,MessageType.UNICAST,message);
        mqueues[receiver_id].put(raw);
    }

    public void broadcast ( int sender_id, String message ) {
        if ((sender_id < 0) || (sender_id >= n_nodes)) {
            System.err.printf("Network::unicast: unknown sender id %d\n",sender_id);
            return;
        }
        Message raw = new Message(sender_id,-1,MessageType.BROADCAST,message);
        for ( int l=0; l<n_nodes; ++l) {
            if (l == sender_id) continue;
            raw.receiver_id = l;
            mqueues[l].put(raw);
        }
    }

    public Message receive ( int receiver_id ) {
        if ((receiver_id < 0) || (receiver_id >= n_nodes)) {
            System.err.printf("Network::unicast: unknown receiver id %d\n",receiver_id);
            return null;
        }
        return mqueues[receiver_id].await();
    }

    public void stop () {
        for ( MessageQueue mq : mqueues )
            mq.stop();
    }

    private final int n_nodes;
    private final MessageQueue[] mqueues;

    private static final Random rgen = new Random();

    //private static Logger logger = Logger.getRootLogger();
}
