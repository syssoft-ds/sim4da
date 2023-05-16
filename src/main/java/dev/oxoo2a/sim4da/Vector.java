package dev.oxoo2a.sim4da;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Vector implements Clock{

    public String getTimeVector() {
        return serializeVector(timeVector);
    }

    public Vector(int nodes, int index)
    {
        HashMap<Integer, Integer> timeVector = new HashMap<>();

        for (int i = 0; i < nodes; i++) {
            timeVector.put(i, 0);
        }
        this.timeVector = timeVector;
        this.index = index;
    }
    @Override
    public int getTimeStamp() {
        return timeVector.get(index);
    }

    @Override
    public void increment()
    {
        int currentValue = getTimeStamp();
        timeVector.put(index, currentValue + 1);
    }

    @Override
    public String printTimeStamp() {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : timeVector.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
        }
        return sb.toString();
    }


    public void update(int senderTime, Object... args)
    {

        int senderIndex = (int) args[0];
        String vector = (String) args[1];
        if(senderIndex == index)
        {
            timeVector.compute(index, (k,v) -> (v==0) ? 1: v + 1);
        }
        else
        {
            HashMap<Integer, Integer> received = deserialize(vector);
            compareVectors(received);
            increment();

        }

    }

    private static synchronized String serializeVector ( HashMap<Integer,Integer> vector ) {
        return serializer.toJson(vector);
    }

    public static HashMap<Integer, Integer> deserialize(String vector)
    {
        Type contentType = new TypeToken<HashMap<Integer,Integer>>() {}.getType();
        HashMap<Integer, Integer> deserializedHashMap = serializer.fromJson(vector, contentType);

        return deserializedHashMap;
    }

    public synchronized void compareVectors(HashMap<Integer, Integer> received)
    {

        for (Integer key : timeVector.keySet()) {
            if (received.containsKey(key) && received.get(key) > timeVector.get(key)) {
                timeVector.put(key, received.get(key));
            }
        }
    }
    protected final int index;
    protected HashMap<Integer, Integer> timeVector;
    private static final Gson serializer = new Gson();
}
