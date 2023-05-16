package dev.oxoo2a.sim4da.clock;

public class LampertClock implements LogicClock{
    private int nodeId;
    private int time;
    public final ClockType type = ClockType.LAMPORT;

    public LampertClock(int nodeId){
        this.nodeId = nodeId;
        this.time = 0;
    }


    @Override
    public void tick() {
        this.time++;

    }

    @Override
    public ClockType getType() {
        return type;
    }

    //
    @Override
    public void synchronize(String timeStamp) {
        for (int i = 0; i < timeStamp.length(); i++) {
            if(timeStamp.charAt(i) == '%' || timeStamp.charAt(i+1) == 'T'){
                int index = -1;
                for (int j = i+2; j < timeStamp.length(); j++) {
                    if (timeStamp.charAt(j)== '\"'){
                        index= j;
                        break;
                    }
                }
                int senderId = Integer.parseInt(timeStamp.substring(i+2, index));
                int timeIndexStart = -1;
                int timeIndexEnd = -1;
                for (int j = index+2; j < timeStamp.length(); j++) {
                    if(timeStamp.charAt(j)== '\"'){
                        timeIndexStart = j;
                        break;
                    }
                }
                for (int j = timeIndexStart+1; j < timeStamp.length(); j++) {
                    if(timeStamp.charAt(j) == '\"'){
                        timeIndexEnd = j;
                    }
                }
                int senderTime = Integer.parseInt(timeStamp.substring(timeIndexStart, timeIndexEnd));
                this.time = Math.max(this.time, senderTime);
            }

        }
    }

    public int getTime() {
        return time;
    }

    public int getNodeId() {
        return nodeId;
    }
}
