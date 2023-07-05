package dev.oxoo2a.sim4da.times;

import dev.oxoo2a.sim4da.Time;

/***
 * Implements the rules of the lamport time
 */
public class LamportTime implements Time {

    private int time;

    public LamportTime(){
        time = 0;
    }

    @Override
    public void incrementMyTime() {
        time++;
    }

    @Override
    public String toString() {
        return time+"";
    }

    @Override
    public void updateTime(String time_sender) {
        int time_s_i = Integer.parseInt(time_sender);
        // max function
        if(time_s_i > time)
            time = time_s_i;
        // increment local time
        incrementMyTime();
    }

    @Override
    public void updateTime(Time time_sender) {
        if(!( time_sender instanceof LamportTime))
            throw new IllegalArgumentException("Wrong time format");
        LamportTime l_time_sender = (LamportTime) time_sender;
        if(l_time_sender.time > time)
            time = l_time_sender.time;
        // increment local time
        incrementMyTime();
    }
}
