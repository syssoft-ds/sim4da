package dev.oxoo2a.sim4da;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.max;

public class SimulationNode extends Node{

    public SimulationNode(int my_id) {
        super(my_id);
        probability = 0.7;
        isActive = true;
        int messages_received = 0;
        int messages_sent = 0;
    }
    @Override
    protected void main() {
        long time = System.currentTimeMillis();
        int loops = 0;
        Random r = new Random();
        Random p = new Random();

        while (stillSimulating())
        {
            sendAMessageToARandomNode(r, p, time);
            loops++;
            emit("Node %d, Loop %d","main",myId,loops);
            Network.Message m_raw = receive();
            if (m_raw == null )
            {

            }
            else
            {
                messages_received++;
                Message m_json = Message.fromJson(m_raw.payload);
                int c = Integer.parseInt(m_json.query("Candidate"));
                clock.update(Integer.parseInt(m_json.query("Time")), Integer.parseInt(m_json.query("Sender")), m_json.query("Vector"));
                emit("Node %d -> Receiver %d, ClockTime on %d %s","clock", c, myId, myId, this.clock.printTimeStamp());
                isActive = true;
                sendAMessageToARandomNode(r, p, time);
            }
            ControlMessage received_control_message = receiveControlMessage(myId);
            if (received_control_message!=null)
            {
                ControlMessage controlMessage = new ControlMessage(ControlMessageType.RESPONSE, myId, isActive, messages_received, messages_sent, received_control_message.getRound());
                sendControlMessage(controlMessage);
                emit("Status of %d %s","clock", myId, isActive);
            }
        }
    }
    private void sendAMessageToARandomNode(Random r, Random p, long time) {

        if (p.nextDouble() < this.probability && isActive) {
            int randomRecipient;
            do {
                randomRecipient = r.nextInt(numberOfNodes());
            } while (randomRecipient == myId);
            clock.increment();
            Message init_message = new Message()
                    .add("Sender", myId)
                    .add("Candidate", r.nextInt(numberOfNodes()))
                    .add("Time", clock.getTimeStamp());
            init_message = this.clock.getTimeVector() != null ? init_message.add("Vector", clock.getTimeVector()) : init_message;
            sendUnicast(randomRecipient, init_message);
            emit("Node %d, ClockTime %s", "clock", myId, this.clock.printTimeStamp());
            probability = max(0, probability - probability * 0.2*(System.currentTimeMillis()-time)/1000);
            messages_sent++;
            isActive = false;
        }
    }


    protected int messages_received;
    protected int messages_sent;
    private boolean isActive;
    private double probability;
    static protected double reductionOfProbability;
}
