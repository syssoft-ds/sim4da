package dev.oxoo2a.sim4da;

public class CountActor extends BaseActor{

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
    public CountActor(int my_id) {
        super(my_id);
    }

    public CountActor(int my_id, boolean trackClock) {
        super(my_id, trackClock);
    }

    public CountActor(int my_id, Clock clock) {
        super(my_id, clock);
    }

    @Override
    protected void handleControlMessage(Message control_cast, int controlID){
        if (controlID == myId)
            return;
        control_cast.add("sent", sent);
        control_cast.add("received", received);
        control_cast.add("isActive", isActive() ? "true" : "false");
        sendUnicast(controlID, control_cast);
    }

    @Override
    protected void handleBasicMessage(Message basic_cast){
        setActive(true);
        received++;
        shouldSend(basic_cast);
    }

    @Override
    protected int sendMessageToRandom(Message u_cast) {
        int r = super.sendMessageToRandom(u_cast);
        sent++;
        return r;
    }
}
