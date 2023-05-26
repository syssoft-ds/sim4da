package dev.oxoo2a.sim4da.logicalClocks;

import dev.oxoo2a.sim4da.Message;

public interface LogicalClock {

    Message receiving(Message m);
    void sending(Message m);
    void event();
    void initClock();
    Message initMessage();

}
