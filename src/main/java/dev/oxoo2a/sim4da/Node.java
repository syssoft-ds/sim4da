package dev.oxoo2a.sim4da;

public abstract class Node implements Simulator2Node {

    public int lemporTime = 0;
    public int[] vectorTime;

    String s="";

    public Node ( int my_id ) {
        this.myId = my_id;
        t_main = new Thread(this::main);
    }

    @Override
    public void setSimulator(Node2Simulator s ) {
        this.simulator = s;
        vectorTime = new int[numberOfNodes()];
        for (int i=0; i<numberOfNodes(); i++) {
            vectorTime[i]=0;
        }
    }

    @Override
    public void start () {
        t_main.start();
    }

    private void sleep ( long millis ) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {};
    }

    protected int numberOfNodes() { return simulator.numberOfNodes(); };

    protected boolean stillSimulating () {
        return simulator.stillSimulating();
    }
    protected void sendUnicast ( int receiver_id, int lamportTime_id, int[] vectorTime, String m ) {
        lemporTime++;
        vectorTime[myId]++;
        simulator.sendUnicast(myId,receiver_id,lamportTime_id,vectorTime,m);
        s="gesendet uni myId: "+myId+" mit LT: "+lemporTime+"\ngesendet broad: "+myId+" mit VT: ";
        s+=toStringVek()+"\n";
    }

    protected void sendUnicast ( int receiver_id, int lamportTime_id, int[] vectorTime, Message m ) {
        lemporTime++;
        vectorTime[myId]++;
        simulator.sendUnicast(myId,receiver_id, lamportTime_id,vectorTime, m.toJson());
        s="gesendet uni myId: "+myId+" mit LT: "+lemporTime+"\ngesendet broad: "+myId+" mit VT: ";
        s+=toStringVek()+"\n";
    }

    protected void sendBroadcast ( String m ) {
        lemporTime++;
        vectorTime[myId]++;
        simulator.sendBroadcast(myId,lemporTime ,vectorTime, m);
        s="gesendet broad myId: "+myId+" mit LT: "+lemporTime+"\ngesendet broad: "+myId+" mit VT: ";
        s+=toStringVek()+"\n";
    }

    protected void sendBroadcast ( Message m ) {
        lemporTime++;
        vectorTime[myId]++;
        simulator.sendBroadcast(myId,lemporTime,vectorTime,m.toJson());
        //System.out.println("gesendet broad: "+myId+" mit LT: "+lemporTime);
        s="gesendet broad myId: "+myId+" mit LT: "+lemporTime+"\ngesendet broad: "+myId+" mit VT: ";
        s+=toStringVek()+"\n";
    }

    protected Network.Message receive () {
        //** Berechnung der ankunfts Zeit:
        // bei Lamport hatte ich mir notiert: newLT = max(lokal, sender)+1
        // dieses +1 hatte ich so in meinen notizen bei VT nicht gefunden
        // dementsprechend ist die Implementierung**//
        String test ="";
        Network.Message nm= simulator.receive(myId);

        //System.out.println("empfangen: "+myId);
        test+="empfangen myId: "+myId+"\n";
        if(nm!=null) {
            //lamport
            //System.out.print("   mit LT aktuell: "+lemporTime);
            test+= "   mit LT aktuell: "+lemporTime;
            int temp = nm.getLamportTime_id();
            //System.out.print(" mit LT sender: "+temp);
            test+=" mit LT sender: "+temp;
            int newLT = Math.max( lemporTime,temp);
            lemporTime=newLT+1;
            //System.out.println(" mit LT neu: "+lemporTime);
            test+=" mit LT neu: "+lemporTime+"\n";
            //vector
            //System.out.print("   mit VT aktuell: ");
            test+= "   mit VT aktuell: ";
            //printVek();
            test+=toStringVek();
            int[] tempv =nm.getVectorTime();
            //System.out.print(" mit VT sender: ");
            test+= " mit VT sender: ";
            //printVek(tempv);
            test+=toStringVek(tempv);
            for (int i =0; i<vectorTime.length; i++) {
                vectorTime[i]=Math.max(tempv[i], vectorTime[i]);
            }
            //int newVTSender = Math.max(tempv[nm.sender_id], vectorTime[nm.sender_id]);
            //int newVTMyId = Math.max(tempv[myId], vectorTime[myId]);
            //vectorTime[nm.sender_id] = newVTSender;
            //vectorTime[myId] = newVTMyId;
            //System.out.print(" mit VT neu: ");
            test+= " mit VT neu: ";
            //printVek();
            test+=toStringVek();
            //System.out.println();
            test+="\n";
            s=test;
        }


        return nm;
    }

    void printVek () {
        for(int i=0; i<vectorTime.length; i++) {
            System.out.print(" "+vectorTime[i]+" ");
        }
    }

    void printVek (int[] v) {
        for(int i=0; i<v.length; i++) {
            System.out.print(" "+v[i]+" ");
        }
    }

    String toStringVek () {
        String temp="";
        for(int i=0; i<vectorTime.length; i++) {
            temp+=" "+vectorTime[i]+" ";
        }
        return temp;
    }
    String toStringVek (int[] v) {
        String temp="";
        for(int i=0; i<v.length; i++) {
            temp+=" "+v[i]+" ";
        }
        return temp;
    }


    void printTestTime () {
        System.out.println(s);
    }

    protected void emit ( String format, Object ... args ) {
        simulator.emit(format,args);
    }
    // Module implements basic node functionality
    protected abstract void main ();

    @Override
    public void stop () {
        try {
            t_main.join();
        }
        catch (InterruptedException ignored) {};
    }

    protected final int myId;
    private Node2Simulator simulator;
    private final Thread t_main;
}
