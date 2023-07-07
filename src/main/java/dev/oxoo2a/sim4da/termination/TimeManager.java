package dev.oxoo2a.sim4da.termination;

public class TimeManager {

    private static long start;
    public static void setTimer(){
        start= System.currentTimeMillis();
    }
    public static long getCurrentSimTime(){
        return System.currentTimeMillis()-start;
    }
}
