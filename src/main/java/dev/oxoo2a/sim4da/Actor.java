package dev.oxoo2a.sim4da;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Actor {
    private boolean active;
    private double p;
    private String lastAction;
    private int senderId;
    private int receiverId;
    private int[] vectorClock;

    public Actor(int numActors) {
        active = true;
        p = 1.0;
        vectorClock = new int[numActors];
    }

    //converging to 0.
    public void updateP(int time) {
        p -= 0.1;
        if (p < 0.0) {
            p = 0.0; // Ensure p does not go below 0
        }
    }

    public boolean isActive(int actorIndex) {
        if (active) {
            System.out.println("[Actor " + actorIndex + " is active.  ]");
        } else {
            System.out.println("[Actor " + actorIndex + " is inactive. Last action: " + lastAction + "]");
        }
        return active;
    }

    public void receiveMessage(int senderId, int receiverId, int[] senderVectorClock) {
        active = true;
        this.senderId = senderId;
        this.receiverId = receiverId;
        lastAction = "Received from Actor " + senderId;

        for (int i = 0; i < vectorClock.length; i++) {
            vectorClock[i] = Math.max(vectorClock[i], senderVectorClock[i]);
        }

        vectorClock[receiverId]++;

        System.out.println("Actor " + receiverId + " received a message from Actor " + senderId +
                " with Vector Clock: " + Arrays.toString(vectorClock));
    }


    public boolean sendMessage(List<Actor> actors, int senderIndex) {
        if (active && Math.random() < p) {
            active = false;
            int receiverIndex = getRandoReceiverWithoutSender(actors, senderIndex);
            int senderId = actors.indexOf(this);
            actors.get(receiverIndex).receiveMessage(senderId, receiverIndex, vectorClock.clone());
            lastAction = "Sent to Actor " + receiverIndex;
            return true;
        }
        return false;
    }

    //to avoid an actor sending messages to themselves.
    private int getRandoReceiverWithoutSender(List<Actor> actors, int senderIndex) {
        int receiverIndex;
        do {
            receiverIndex = new Random().nextInt(actors.size());
        } while (receiverIndex == senderIndex);
        return receiverIndex;
    }
}
