package dev.oxoo2a.sim4da;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import static dev.oxoo2a.sim4da.ControlMessageType.REQUEST;
import static java.lang.Thread.sleep;

public class DoubleCountTerminator{

    public static int broadcast_run = 0;
    private final int n_Nodes;
    public Thread thread;
    private int numberSent;
    static int counter;
    static int numberReceived;
    private final Semaphore await_message;
    protected ArrayList<ControlMessage> received_array;
    private Node2Simulator  simulator;
    static boolean stop;

    public DoubleCountTerminator(int numberOfNodes)
    {
        n_Nodes = numberOfNodes;
        numberSent = 0;
        received_array = new ArrayList<ControlMessage>();
        await_message = new Semaphore(0);
        thread = new Thread(this::main);
        thread.setName("Observer");

    }
    public void setSimulator(Node2Simulator s ) {
        this.simulator = s;
    }

    public void broadcastControlMessage() {
        broadcast_run++;
        for(int i=0;i<n_Nodes;i++)
        {
            ControlMessage cm = new ControlMessage(broadcast_run, REQUEST, i);
            simulator.sendControlMessage(cm);
            numberSent++;
        }

    }
    synchronized void update(ControlMessage controlMessage)
    {
            received_array.add(controlMessage);
            numberReceived++;
            if(numberReceived==numberSent)
            {
                counter++;
                await_message.release();
            }
    }
    public void main()
    {
        while (broadcast_run<=2) {

            if (stop) break;

            if (counter <2 && broadcast_run<2)
            {
                broadcastControlMessage();

                try {
                    await_message.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                simulator.emit("number sent %d", "doublecount", numberSent);
                simulator.emit("received %d", "doublecount", numberReceived);
                numberReceived= 0;
                numberSent = 0;
            }
            else if (counter==2 && checkReceivedQueue(received_array)) {

                simulator.updateStatus();
                simulator.emit("TERMINATING", "doublecount","after 2 runs");
                System.exit(0);
                break;
            }
            else if (counter==2 && !checkReceivedQueue(received_array))
            {
                simulator.emit("DID 2 RUNS, DIDN'T TERMINATE", "doublecount");
                break;
            }

        }
    }
    public void start () {

        thread.start();
    }

    public void stop () {

            stop = true;
            await_message.release();

    }
    public void end() throws InterruptedException {
        thread.join();
    }

    public synchronized static boolean checkReceivedQueue(ArrayList<ControlMessage> arr) {
        int round1_received = 0;
        int round2_received = 0;
        int round1_sent = 0;
        int round2_sent = 0;
        boolean[] status = new boolean[arr.size()];

        for(int i=0; i<arr.size(); i++)
        {
            if(arr.get(i).getRound() == 1)
            {
                round1_received += 1;
                round1_sent += 1;

            }
            else
            {
                round2_received += 1;
                round2_sent += 1;
                status[i] = arr.get(i).isActive();
            }
        }

        boolean allFalse = true;
        for (boolean value : status) {
            if (value) {
                allFalse = false;
                break;
            }
        }
        return allFalse && (round1_received == round1_sent && round1_sent == round2_received && round2_received == round2_sent);
    }

}
