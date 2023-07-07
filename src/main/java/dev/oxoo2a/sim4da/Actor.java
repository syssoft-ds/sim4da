package dev.oxoo2a.sim4da;

import java.util.Random;

public class Actor extends Node{

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive = false;

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    private int received = 0;

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    private int sent = 0;

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
        if (probability > 1f)
            this.probability  = 1f;
        else if (probability < 0f)
            this.probability  = 0f;
    }

    private float probability = 1.0f;
    final float convergence = 0.00005f;

    public Actor(int my_id) {
        super(my_id, false);
    }
    public Actor(int my_id, boolean trackClock) {
        super(my_id, trackClock);
    }
    public Actor( int my_id, Clock clock ) { super(my_id, clock);}

    @Override
    protected void main() {

        Message basic_cast = new Message().add("Sender",myId).add("isControl", "false");
        Message control_cast = new Message().add("Sender",myId).add("isControl", "true");

        // random decides if actor should be active on simulation start
        Random r = new Random();
        float random = r.nextFloat();

        // (myId == 0) makes sure the simulation runs and not all actors are inactive by chance
        if (myId == 0 || random >= 0.7f)
            shouldSend(basic_cast);

        while (stillSimulating()) {
            // receiving
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message

            Message m_json = Message.fromJson(m_raw.payload);
            boolean isControl = Boolean.parseBoolean(m_json.query("isControl"));

            // sending
            if (!isControl) {
                received++;
                isActive = true;
                shouldSend(basic_cast);
            // send control
            }else{
                int controlID = Integer.parseInt(m_json.query("Sender"));
                if (controlID == myId)
                    continue;
                control_cast.add("sent", sent);
                control_cast.add("received", received);
                control_cast.add("isActive", isActive ? "true" : "false");
                sendUnicast(controlID, control_cast);
            }
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
        int random = r.nextInt(numberOfNodes()-1) +1 ;
        sent++;

        sendUnicast(random, u_cast);
    }

}
