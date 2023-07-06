package dev.oxoo2a.sim4da;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class VectorControlTerminator
{

    public VectorControlTerminator()
    {
        thread = new Thread(this::main);
        thread.setName("VectorControlTerminator");
    }

    public void setSimulator(Node2Simulator s ) {
        this.simulator = s;
    }

    private void main()
    {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(true)
        {
            if (stop) break;

            if(checkIfFinilised())
            {
                simulator.updateStatus();
                simulator.emit("TERMINATING BASED ON VECTOR CONTROL", "clock");
                System.exit(0);
                break;

            }
            runs++;
        }
    }

    private boolean checkIfFinilised()
    {
        return simulator.checkIfFinilised();
    }

    public void start () {

        thread.start();
    }
    public void end() throws InterruptedException {
        thread.join();
    }

    public void stop () {

        stop = true;

    }
    static boolean stop;
    public Thread thread;
    private Node2Simulator simulator;
    private static int runs = 0;
}
