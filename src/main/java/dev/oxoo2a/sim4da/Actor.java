package dev.oxoo2a.sim4da;

import java.util.List;
import java.util.Random;

public class Actor {
    private boolean active;
    private double p;

    public Actor() {
        active = true;
        p = 1.0;
    }

    public void updateP(int time) {
        p = 1.0 / time;
    }

    public boolean isActive() {
        return active;
    }

    public void receiveMessage(int senderId) {
        active = true;
       // System.out.println("Actor " + senderId + " received a message.");
    }

    public boolean sendMessage(List<Actor> actors) {
        if (active && Math.random() < p) {
            active = false;
            int receiverIndex = new Random().nextInt(actors.size());
            int senderId = actors.indexOf(this); //Index des aktuellen Akteurs
            actors.get(receiverIndex).receiveMessage(senderId);
            return true;
        }
        return false;
    }
}
