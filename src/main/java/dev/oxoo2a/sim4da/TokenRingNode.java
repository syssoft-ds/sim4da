package dev.oxoo2a.sim4da;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TokenRingNode extends Node{
    public TokenRingNode(int my_id) {
        super(my_id);
        if(useLamportClock)
            lamportClock=0;
        if(useVectorTime){
            for(int i=0; i<knowledgeOfTheTotalAmountOfNodes; i++){
                if(i==myId){
                    vectorTime.put(IDNameHelper.computerIDTime + i, 1);

                }else {
                    vectorTime.put(IDNameHelper.computerIDTime + i, 0);
                }
            }


        }
    }
    private int lamportClock;
    public static boolean useLamportClock= false;
    public static boolean useVectorTime= false;
    public static int knowledgeOfTheTotalAmountOfNodes= 0;

    private HashMap<String, Integer> vectorTime= new HashMap<>();



    @Override
    protected void main() {
        Message m = new Message();
        if(myId == 0){


            m.add(IDNameHelper.counter, 0);

            if (useLamportClock) {
                m.add(IDNameHelper.lamportClock, ++lamportClock);
                m.add(IDNameHelper.computerID, myId);
            }
            if(useVectorTime){
                for(String key: vectorTime.keySet()){
                    m.add(key, vectorTime.get(key));
                }
            }

            sendUnicast(1, m);

        }
        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) break;
            m = Message.fromJson(m_raw.payload);
            if(useLamportClock) {
                    this.lamportClock = (Math.max(this.lamportClock, Integer.parseInt(m.getMap().get(IDNameHelper.lamportClock)))) + 1;
            }else if(useVectorTime){

                int myTimeAdjusted = vectorTime.get(IDNameHelper.computerIDTime +myId)+1;
                int maxTimeFromMessage= retrieveMaxTimeValue(m.getMap(),myTimeAdjusted);

                for (String key: m.getMap().keySet()){
                    if(key.contains(IDNameHelper.computerIDTime)){
                        if(key.equals(IDNameHelper.computerIDTime+myId))
                            continue;
                        vectorTime.put(key, Integer.parseInt(m.getMap().get(key)));
                    }
                }
                vectorTime.put(IDNameHelper.computerIDTime+myId, maxTimeFromMessage);
                for (String key: vectorTime.keySet()){
                        m.add(key, Integer.parseInt(String.valueOf(vectorTime.get(key))));
                }
            }

            int counter = Integer.parseInt(m.query(IDNameHelper.counter));
            emit("%d: counter == %d", myId, counter);
            counter++;
            m.add(IDNameHelper.counter, counter);

            if(useLamportClock) {
                m.add(IDNameHelper.lamportClock, ++lamportClock);
                m.add(IDNameHelper.computerID, myId);

            }else if(useVectorTime){
                int myTime= vectorTime.get(IDNameHelper.computerIDTime +myId);
                vectorTime.put(IDNameHelper.computerIDTime +myId, ++myTime);
                m.getMap().put(IDNameHelper.computerIDTime +myId, String.valueOf(myTime));
            }


            sendUnicast((myId + 1) % numberOfNodes(),m);
        }
    }
    private Integer retrieveMaxTimeValue(Map<String, String> map, int myTime){
        Optional<Integer> optionalMaxTime= map
                .keySet()
                .stream()
                .filter(e-> e.contains(IDNameHelper.computerIDTime))
                .collect(Collectors.toList())
                .stream()
                .map(map::get)
                .map(Integer::parseInt)
                .max(Integer::compare);

        return optionalMaxTime.map(integer -> Math.max(myTime, integer)).orElse(myTime);
    }

    private static class IDNameHelper{
        private static final String lamportClock="lamportClock";
        private static final String counter= "counter";
        private static final String computerIDTime = "computerIDTime";
        private static final String computerID = "computerID";

    }

}