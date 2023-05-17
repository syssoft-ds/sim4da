package dev.oxoo2a.sim4da;

public class TokenRingNode extends Node{
    public TokenRingNode(int my_id) {
        super(my_id);
        lamportClock=0;
    }
    private int lamportClock;

    @Override
    protected void main() {
        Message m = new Message();
        if(myId == 0){
            m.add(IDNameHelper.counter, 0);
            m.add(IDNameHelper.lamportClock, ++lamportClock);
            System.out.println("Node "+ myId+" will send the following now");
            for (String key: m.getMap().keySet()){
                System.out.println(key+": "+ m.getMap().get(key));
            }
            sendUnicast(1, m);

        }
        while (true){
            Network.Message m_raw = receive();
            if(m_raw == null) break;
            m = Message.fromJson(m_raw.payload);
            if(m.getMap()!=null){
                System.out.println("Node "+ myId +" received following");
                this.lamportClock= (Math.max(this.lamportClock, Integer.parseInt(m.getMap().get(IDNameHelper.lamportClock)))) +1;
                for(String key: m.getMap().keySet()){
                    System.out.println(key+": "+ m.getMap().get(key));
                }
            }else{
                System.out.println("Map is null");
            }

            int counter = Integer.parseInt(m.query(IDNameHelper.counter));
            emit("%d: counter == %d", myId, counter);
            counter++;
            m.add(IDNameHelper.counter, counter);
            m.add(IDNameHelper.lamportClock, ++lamportClock);
            sendUnicast((myId + 1) % numberOfNodes(),m);
        }
    }

    public class IDNameHelper{
        static String lamportClock="lamportClock";
        static String counter= "counter";
    }
}