package dev.oxoo2a.sim4da.clock;

public class ClockFactory {

    public static LogicClock create(int id, ClockType type){

        return switch (type) {
            case LAMPORT -> new LampertClock(id);
            case VECTOR -> new VectorClock(id);
        };
    }

}
