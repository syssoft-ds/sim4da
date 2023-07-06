package dev.oxoo2a.sim4da;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

public class Simulator implements Node2Simulator {



    public static Simulator createDefaultSimulator (int n_nodes ) {
        return new Simulator(n_nodes, "sim4da", true, true, true, System.out);
    }

    public static Simulator createSimulator_Log4j2 ( int n_nodes ) {
        return new Simulator(n_nodes,"sim4da", true,true,true,null);
    }

    public Simulator ( int n_nodes, String name, boolean ordered, boolean enableTracing, boolean useLog4j2, PrintStream alternativeDestination ) {
        this.n_nodes = n_nodes;
        tracer = new Tracer(name,ordered,enableTracing,useLog4j2,alternativeDestination);
        doubleCountTerminator = new DoubleCountTerminator(n_nodes);
        doubleCountTerminator.setSimulator(this);
        controlVectorTerminator = new VectorControlTerminator();
        controlVectorTerminator.setSimulator(this);
        network = new Network(n_nodes,tracer);
        nodes = new HashMap<Integer, Simulator2Node>(n_nodes);
        for (int n_id = 0; n_id < n_nodes; ++n_id)
            nodes.put(n_id, null);
    }

    @Override
    public int numberOfNodes() {
        return n_nodes;
    }

    public void attachNode (int id, Simulator2Node node ) {
        if ((0 <= id) && (id < n_nodes))
            nodes.replace(id,node);
    }

    public void runSimulation (int duration, String type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
        // Check that all nodes are attached
        /*for (Simulator2Node n : nodes.values()) {
            if (n == null) throw new InstantiationException();
            n.setSimulator(this);
            n.createClockByClass(type, nodes.size(), n.ge);
        }*/
        for (Map.Entry<Integer, Simulator2Node> elem: nodes.entrySet())
        {
            Simulator2Node n = elem.getValue();
            if (n == null) throw new InstantiationException();
            n.setSimulator(this);
            n.createClockByClass(type, nodes.size(), elem.getKey());
        }

        tracer.emit("main","Simulator::runSimulation with %d nodes for %d seconds",n_nodes,duration);
        is_simulating = true;
        nodes.values().forEach(Simulator2Node::start);
        doubleCountTerminator.start();
        // Wait for the required duration
        try {
            Thread.sleep(duration * 1000L);
        }
        catch (InterruptedException ignored) {}
        is_simulating = false;
        doubleCountTerminator.stop();
        doubleCountTerminator.end();
        controlVectorTerminator.stop();
        controlVectorTerminator.end();
        // Stop network - release nodes waiting in receive ...
        network.stop();
        // Tell all nodes to stop and wait for the threads to terminate
        nodes.values().forEach(Simulator2Node::stop);

        tracer.emit("main","Simulator::runSimulation finished");
    }

    public void updateStatus()
    {
        is_simulating = false;
    }

    @Override
    public boolean stillSimulating() {
        return is_simulating;
    }

    @Override
    public void sendUnicast(int sender_id, int receiver_id, String m) {
        network.unicast(sender_id,receiver_id,m);
    }

    @Override
    public void sendUnicast ( int sender_id, int receiver_id, Message m ) {
        network.unicast(sender_id,receiver_id,m.toJson());
    }

    @Override
    public void sendBroadcast ( int sender_id, String m ) {
        network.broadcast(sender_id,m);
    }

    @Override
    public void sendBroadcast ( int sender_id, Message m ) {
        network.broadcast(sender_id,m.toJson());
    }

    @Override
    public synchronized void passControlMessage(ControlMessage controlMessage) {
        emit("sent a control message to the observer from %d", "doublecount", controlMessage.getId());
        doubleCountTerminator.update(controlMessage);
    }

    @Override
    public ControlMessage receiveControlMessage(int id) {
        return network.getControlMessage(id);
    }

    @Override
    public Network.Message receive ( int receiver_id ) {
        return network.receive(receiver_id);
    }
    @Override
    public void sendControlMessage(ControlMessage controlMessage) {
        emit("sent a control message to the node %d", "clock", controlMessage.getId());
        network.addToControlQueue(controlMessage);
    }
    @Override
    public boolean checkIfFinilised() {

        synchronized (network.controlVector){
            if(network.controlVector == null)
            {
                return false;
            }
            else{
                for (int[] value : network.controlVector.values())
                {
                    return (Arrays.stream(value).allMatch(element -> element == 0));
                }
            }
            return false;
        }

    }
    @Override
    public void sendControlVectorToNetwork(int randomRecipient, int[] controlVector)
    {
        network.controlVector.put(randomRecipient, controlVector);
    }

    @Override
    public HashMap<Integer, int[]> returnControlVector(int Id) {
       // if(network.controlVector.isEmpty()) return null;

        if(network.controlVector.containsKey(Id))
        {
            HashMap<Integer, int[]> controlVector = new HashMap<>(network.controlVector);
            network.controlVector.clear();
            return controlVector;
        }

        return null;
    }

    @Override
    public void emit (String format, String logType, Object ... args) {
        tracer.emit(format,logType, args);
    }
    private DoubleCountTerminator doubleCountTerminator;
    private final int n_nodes;
    private final Tracer tracer;
    private final Network network;
    private final HashMap<Integer, Simulator2Node> nodes;
    private boolean is_simulating = false;

    private VectorControlTerminator controlVectorTerminator;
}
