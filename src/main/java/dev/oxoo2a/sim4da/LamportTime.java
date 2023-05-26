package dev.oxoo2a.sim4da;

/***
 * Implements the rules of the lamport time
 */
public class LamportTime implements Time{

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
}
