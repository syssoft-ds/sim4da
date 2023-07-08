package dev.oxoo2a.sim4da;

import java.util.Random;

public abstract class BaseActor extends Node{

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive = false;

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

    public BaseActor(int my_id) {
        super(my_id, false);
    }
    public BaseActor(int my_id, boolean trackClock) {
        super(my_id, trackClock);
    }
    public BaseActor(int my_id, Clock clock ) { super(my_id, clock);}

    @Override
    protected void main() {

        Message basic_cast = new Message().add("Sender",myId).add("isControl", "false");
        Message control_cast = new Message().add("Sender",myId).add("isControl", "true");

        // random decides if actor should be active on simulation start
        Random r = new Random();
        float random = r.nextFloat();

        // (myId == 0) is reserved for the control actor and it will definitly will send a message to the network
        if (myId == 0 || random >= 0.7f)
            shouldSend(basic_cast);

        while (stillSimulating()) {
            // receiving
            Network.Message m_raw = receive();
            if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message

            Message m_json = Message.fromJson(m_raw.payload);
            boolean isControl = Boolean.parseBoolean(m_json.query("isControl"));

            // send basic
            if (!isControl)
                handleBasicMessage(basic_cast);
            // send control
            else{
                int controlID = Integer.parseInt(m_json.query("Sender"));
                handleControlMessage(control_cast, controlID);
            }
        }

    }

    protected void shouldSend(Message u_cast){
        Random r = new Random();
        float random = r.nextFloat();
        while (random <= probability ){
            sendMessageToRandom(u_cast);
            probability -= convergence;
            random = r.nextFloat();
        }
        isActive = false;
    }

    protected int sendMessageToRandom(Message u_cast){
        Random r = new Random();
        int random = r.nextInt(numberOfNodes()-1) +1 ;
        sendUnicast(random, u_cast);
        return random;
    }

    protected abstract void handleControlMessage(Message control_cast, int controlID);
    protected abstract void handleBasicMessage(Message basic_cast);
}
