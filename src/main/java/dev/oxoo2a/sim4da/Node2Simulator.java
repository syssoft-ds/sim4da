package dev.oxoo2a.sim4da;

public interface Node2Simulator {
    int numberOfNodes();
    boolean stillSimulating ();
    void sendUnicast ( int sender_id, int receiver_id,int lamportTime_id,int[]vectorTime, String m );
    void sendUnicast ( int sender_id, int receiver_id,int lamportTime_id,int[]vectorTime, Message m );
    void sendBroadcast ( int sender_id, int lamportTime_id,int[]vectorTime,String m );
    void sendBroadcast ( int sender_id,int lamportTime_id,int[]vectorTime, Message m );
    Network.Message receive ( int my_id );
    void emit ( String format, Object ... args );
}
