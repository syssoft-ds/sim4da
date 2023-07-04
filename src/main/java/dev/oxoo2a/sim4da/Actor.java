package dev.oxoo2a.sim4da;

import java.util.Random;

public class Actor extends Node{

    private boolean isActive = false;
    private int received = 0;
    private int sent = 0;

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
        if (probability > 1f)
            probability = 1f;
        else if (probability < 0f)
            probability = 0f;
    }

    private float probability = 1.0f;
    final float convergence = 0.1f;

    public Actor(int my_id) {
        super(my_id, false);
    }
    public Actor(int my_id, boolean trackClock) {
        super(my_id, trackClock);
    }
    public Actor( int my_id, Clock clock ) { super(my_id, clock);}

    @Override
    protected void main() {

        Message u_cast = new Message().add("Sender",myId);

        // random decides if actor should be active on simulation start
        Random r = new Random();
        float random = r.nextFloat();

        // (myId == 0) makes sure the simulation runs and not all actors are inactive by chance
        if (myId == 0 || random >= 0.7f)
            shouldSend(u_cast);

        while (stillSimulating()) {
            // receiving
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message
            received++;
            isActive = true;

            // sending
            shouldSend(u_cast);
        }
    }

    private void shouldSend(Message u_cast){
        Random r = new Random();
        float random = r.nextFloat();
        while (random <= probability ){
            sendMessageToRandom(u_cast);
            probability -= convergence;
            random = r.nextFloat();
        }
        isActive = false;
    }

    private void sendMessageToRandom(Message u_cast){
        Random r = new Random();
        int random = r.nextInt(numberOfNodes()-1) ;
        sendUnicast(random, u_cast);
        sent++;
    }
}
