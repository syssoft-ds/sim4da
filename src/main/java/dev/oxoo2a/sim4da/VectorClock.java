package dev.oxoo2a.sim4da;


import java.util.Arrays;
import java.util.stream.Stream;

public class VectorClock implements Clock{
    private int[] vector;
    private int node_id;

    public VectorClock(int node_id, int size){
        // sets the node_id to 0 if the size is smaller than node_id
        if (node_id < size)
            this.node_id = node_id;

        vector = new int[size];

        for (int i = 0; i < size; i++) {
            vector[i] = 0;
        }
    }
    @Override
    public void increase() {
        vector[node_id]++;
    }

    @Override
    public void synchronize(Network.Message m) {
        increase();
        Message m_json = Message.fromJson(m.payload);
        String receivedTime = m_json.query("Time");
        int[] receivedVector = stringToVector(receivedTime);

        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.max(vector[i],receivedVector[i]);
        }
    }

    @Override
    public String getTime() {
        return Arrays.toString(vector);
    }

    private int[] stringToVector(String str){
        return Stream.of(str.replaceAll("[\\[\\]\\ ]", "").split(",")).mapToInt(Integer::parseInt).toArray();
    }
}
