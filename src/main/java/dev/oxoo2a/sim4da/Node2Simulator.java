package dev.oxoo2a.sim4da;

import java.util.HashMap;

public interface Node2Simulator {
    int numberOfNodes();
    boolean stillSimulating ();
    void sendUnicast ( int sender_id, int receiver_id, String m );
    void sendUnicast ( int sender_id, int receiver_id, Message m );
    void sendBroadcast ( int sender_id, String m );
    void sendBroadcast ( int sender_id, Message m );
    void passControlMessage(ControlMessage cm);
    ControlMessage receiveControlMessage (int id);
    Network.Message receive ( int my_id );
    void emit ( String format, String logType, Object ... args );
    void sendControlMessage(ControlMessage controlMessage);
    void updateStatus();
}
