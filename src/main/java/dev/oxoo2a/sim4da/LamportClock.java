package dev.oxoo2a.sim4da;

import java.util.Vector;

public class LamportClock implements Clock{

    int lc = 0;

    public LamportClock(){}

    @Override
    public void increase() {
        lc++;
    }

    @Override
    public void synchronize(Network.Message m) {
        Message m_json = Message.fromJson(m.payload);
        int receivedTime = Integer.parseInt(m_json.query("Time"));
        lc = Math.max(lc+1, receivedTime);
    }

    @Override
    public String getTime() {
        return String.valueOf(lc);
    }
}
