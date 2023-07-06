package dev.oxoo2a.sim4da;

import java.util.ArrayList;
import java.util.List;

public class ControlMessage {

    private int round;
    protected int received;
    protected int sent;
    protected boolean isActive;
    protected int id;

    protected ControlMessageType type;

    public ControlMessage(int round, ControlMessageType type, int id)
    {
        this.round = round;
        this.type = type;
        this.id = id;
    }

    public ControlMessage(ControlMessageType type, int id, boolean status, int r, int s, int round)
    {
        this.round = round;
        this.type = type;
        this.id = id;
        this.isActive = status;
        this.received = r;
        this.sent = s;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getId() {
        return id;
    }

    public int getRound() { return round;}

    public int getReceived() { return received;}

    public int getSent() { return sent;}

}
