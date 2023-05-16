package dev.oxoo2a.sim4da.clock;

public class ClockUtil {

    public static LogicClock create(int id, ClockType type){

        return switch (type) {
            case LAMPORT -> new LampertClock(id);
            case VECTOR -> new VectorClock(id);
        };
    }

    //{"%T0":"389","%T1":"388","%T2":"388","%T3":"388","%T4":"388","counter":"1940"}
    public static void synchronize(LogicClock lc, String s){

    }

}
