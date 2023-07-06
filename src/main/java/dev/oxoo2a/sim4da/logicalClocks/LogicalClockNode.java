package dev.oxoo2a.sim4da.logicalClocks;

import dev.oxoo2a.sim4da.Main;
import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.HashMap;
import java.util.Random;

public abstract class LogicalClockNode extends Node implements LogicalClock{

    private Class<?extends LogicalClockNode> my_ClockType;
    private HashMap<String, Integer> clockTime= new HashMap<>();
    public int knowledgeOfTheTotalAmountOfNodes;
    private boolean isActive=true;
    private Random random= new Random();
    private double probability=1.0;
    private double constant= 0.02;
    private int messagesSend=1; //starting at 0 results in probability of 1 when using exponentialfunction -> that would mean every node sends a message at the start


    public LogicalClockNode(int my_id, Class<?extends LogicalClockNode> my_ClockType, int knowledgeOfTheTotalAmountOfNodes) {
        super(my_id);
        this.my_ClockType=my_ClockType;
        this.knowledgeOfTheTotalAmountOfNodes=knowledgeOfTheTotalAmountOfNodes;
        initClock();

    }



    @Override
    protected void main() {
        Message m;



    }

    @Override
    public Message initMessage() {
        Message m= new Message();
        m.add(IDNameHelper.counter, 0);

        if (my_ClockType.isAssignableFrom(LamportClockNode.class)) {
            int currentLamportClock= clockTime.get(IDNameHelper.lamportClock);
            m.add(IDNameHelper.lamportClock, ++currentLamportClock);
            clockTime.put(IDNameHelper.lamportClock,currentLamportClock);
            m.add(IDNameHelper.computerID, myId);
        }
        else if(my_ClockType.isAssignableFrom(VectorClockNode.class)){
            for(String key: clockTime.keySet()){
                m.add(key, clockTime.get(key));
            }
        }
        return m;
    }

    @Override
    public void initClock() {
        if(my_ClockType.isAssignableFrom(LamportClockNode.class)){

            clockTime.put(IDNameHelper.lamportClock,0);

        }else if(my_ClockType.isAssignableFrom(VectorClockNode.class)){

            for(int i=0; i<knowledgeOfTheTotalAmountOfNodes; i++){
                if(i==myId){
                    clockTime.put(IDNameHelper.computerIDTime + i, 1);

                }else {
                    clockTime.put(IDNameHelper.computerIDTime + i, 0);
                }
            }
        }
    }

    @Override
    public Message receiving(Message m) {
        if(my_ClockType.isAssignableFrom(VectorClockNode.class)){

            int newClockTime= clockTime.get(IDNameHelper.computerIDTime+myId);
            newClockTime++;
            clockTime.put(IDNameHelper.computerIDTime+myId, newClockTime);
            for (String key: m.getMap().keySet()){
                if(key.contains(IDNameHelper.computerIDTime)){
                    clockTime.put(key, Math.max(clockTime.get(key),Integer.parseInt(m.getMap().get(key))));
                    m.add(key, clockTime.get(key));
                }
            }
            return m;

        }else if(my_ClockType.isAssignableFrom(LamportClockNode.class)){

            int newClockTime= Math.max(clockTime.get(IDNameHelper.lamportClock), Integer.parseInt(m.getMap().get(IDNameHelper.lamportClock ))) + 1;
            clockTime.put(IDNameHelper.lamportClock, newClockTime);
            m.add(IDNameHelper.lamportClock,newClockTime);
            return m;

        }
        throw new IllegalStateException("No Clock Class defined");
    }

    @Override
    public void sending(Message m) {

        if(my_ClockType.isAssignableFrom(VectorClockNode.class)){

            int myTime= clockTime.get(IDNameHelper.computerIDTime +myId);
            clockTime.put(IDNameHelper.computerIDTime +myId, ++myTime);
            m.getMap().put(IDNameHelper.computerIDTime +myId, String.valueOf(myTime));

            return;
        }else if(my_ClockType.isAssignableFrom(LamportClockNode.class)){

            int newClockTime= clockTime.get(IDNameHelper.lamportClock);
            m.add(IDNameHelper.lamportClock, ++newClockTime);
            clockTime.put(IDNameHelper.lamportClock, newClockTime);
            m.add(IDNameHelper.computerID, myId);

            return;
        }

        throw new IllegalStateException("No Clock Class defined");

    }

    @Override
    public void event() {
        if(my_ClockType.isAssignableFrom(VectorClockNode.class)){

        }else if(my_ClockType.isAssignableFrom(LamportClockNode.class)){

        }
    }

    private static class IDNameHelper{
        private static final String lamportClock= "lamportClock";
        private static final String counter= "counter";
        private static final String computerIDTime = "computerIDTime";
        private static final String computerID = "computerID";

    }
}
