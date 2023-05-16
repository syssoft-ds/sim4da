package dev.oxoo2a.sim4da.clock;

/*TODO: Klasse in ABstrakt ändern. Gemeinsame Methode "synchronize(String) mit Tokenizer findet alle ID - Timestamp Paare"
*  Über ClockType Kondition verrechnen
*/

import java.time.Clock;
import java.util.HashMap;
import java.util.StringTokenizer;

public abstract class LogicClock {

    protected int nodeId;
    protected int time;
    public final ClockType type;
    protected HashMap<Integer, Integer> tempTimestamps = new HashMap<>();

    public LogicClock(int nodeId, ClockType type){
        this.nodeId = nodeId;
        this.type = type;
        this.time =0;

    }

    public ClockType getType(){
        return this.type;
    }
    public void tick(){
        this.time++;
    }
    public void synchronize(String timeStamp){
        StringTokenizer tokenizer = new StringTokenizer(timeStamp, ",");
        tempTimestamps.clear();
        while (tokenizer.hasMoreTokens()){
            int senderId = -1;
            String token = tokenizer.nextToken();
            String s = "";
            if (token.contains("%T")) {
                StringTokenizer subTokenizer = new StringTokenizer(token, ":");
                s = subTokenizer.nextToken();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == 'T') {
                        senderId = Integer.parseInt(s.substring(i + 1, s.length() - 1));
                        break;
                    }

                }

                s = subTokenizer.nextToken();
                int senderTime = Integer.parseInt(s.substring(1, s.length()-1));
                System.out.println("Extracted TimeStamp Data:");
                System.out.println("[ " + senderId + " : " + senderTime + " ] -|- MyID: "+ this.nodeId);
                tempTimestamps.put(senderId, senderTime);
            }
        }
    }

    public int getTime(){
        return this.time;
    }

}
