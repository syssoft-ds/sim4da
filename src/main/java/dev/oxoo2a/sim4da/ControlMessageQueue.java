package dev.oxoo2a.sim4da;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class ControlMessageQueue
{

    public ControlMessageQueue()
    {
        queue = new ArrayList<ControlMessage>();

    }
    public synchronized void put ( ControlMessage r )
    {
        synchronized (queue) {
            queue.add(r);
        }
    }
    public ControlMessage get(int id)  {
        while(true)
        {
            try {
            synchronized (queue) {
                ControlMessage result = null;
                for (ControlMessage cm : queue) {
                    if (cm.getId() == id) {
                        result = cm;
                        break;
                    }
                }
                if (result != null) {
                    queue.remove(result);
                    return result;
                } else {
                    return null;
                }
            }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    protected static ArrayList<ControlMessage> queue;
}
