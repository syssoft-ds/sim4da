package dev.oxoo2a.sim4da;

public interface Node2Simulator {
    int numberOfNodes();
    boolean stillSimulating ();
    void sendUnicast ( int sender_id, int receiver_id, String m );
    void sendUnicast ( int sender_id, int receiver_id, Message m );
    void sendBroadcast ( int sender_id, String m );
    void sendBroadcast ( int sender_id, Message m );
    Network.Message receive ( int my_id );
    void emit ( String format, Object ... args );
}
