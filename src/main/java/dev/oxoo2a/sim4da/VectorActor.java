package dev.oxoo2a.sim4da;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class VectorActor extends BaseActor{

    public int[] vector;

    public VectorActor(int my_id, int n) {
        super(my_id);
        vector = new int[n];
    }

    public VectorActor(int my_id, boolean trackClock, int n) {
        super(my_id, trackClock);
        vector = new int[n];

    }

    public VectorActor(int my_id, Clock clock, int n) {
        super(my_id, clock);
        vector = new int[n];
    }

    @Override
    protected void handleControlMessage(Message control_cast, int controlID){
        if (controlID == myId)
            return;
        control_cast.add("vector", vectorToString(vector));
        control_cast.add("isActive", isActive() ? "true" : "false");
        sendUnicast(controlID, control_cast);
    }

    @Override
    protected void handleBasicMessage(Message basic_cast){
        setActive(true);
        vector[myId] = vector[myId] - 1 ;
        shouldSend(basic_cast);
    }

    @Override
    protected int sendMessageToRandom(Message u_cast) {
        int r = super.sendMessageToRandom(u_cast);
        vector[r] = vector[r] + 1;
        return r;
    }

    protected String vectorToString(int[] vector) {
        return Arrays.toString(vector);
    }
    protected int[] stringToVector(String str){
        return Stream.of(str.replaceAll("[\\[\\]\\ ]", "").split(",")).mapToInt(Integer::parseInt).toArray();
    }
}
