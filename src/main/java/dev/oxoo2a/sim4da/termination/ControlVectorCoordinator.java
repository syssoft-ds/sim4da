package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.StringTokenizer;

public class ControlVectorCoordinator extends Node {

    boolean prompt = true;
    boolean finished = false;

    public ControlVectorCoordinator(int my_id) {
        super(my_id);
    }

    @Override
    protected void main() {
        while (true) {
            Message m;
            //send round message only after the message arrived back at coordinator (and in the beginning)
            if(prompt){
                prompt=false;
                m=new Message();
                String vectorString="";
                // At the start of the control vector round trip, initialize all entries to 0
                for (int i = 0; i < TerminationMain.n_nodes-1; i++) {
                    String subString = i+":"+0+";";
                    vectorString = vectorString+subString;
                }
                vectorString = vectorString + (TerminationMain.n_nodes-1)+ ":" + 0;
                m.add("type" , "control_vector");
                m.add("vector",  vectorString);
                sendUnicast(0,m);
            }

            //wait for response
            Network.Message m_raw = receive();
            if(m_raw!=null){


                m = Message.fromJson(m_raw.payload);
                String vectorString = m.query("vector");
                StringTokenizer tokenizer = new StringTokenizer(vectorString, ";");
                finished = true;
                //check if vector came back as zero
                while(tokenizer.hasMoreTokens()){
                    String entry = tokenizer.nextToken();
                    StringTokenizer subTokenizer = new StringTokenizer(entry,":");
                    int id = Integer.parseInt(subTokenizer.nextToken());
                    int value = Integer.parseInt(subTokenizer.nextToken());
                    if(value != 0){
                        finished = false;
                        break;
                    }
                }
                if(finished){
                    System.out.println("CONTROL VECTOR SAYS SYSTEM TERMINATED");
                    System.exit(0);
                }else{
                    // restart trailing control vector
                    prompt = true;
                }
            }
        }
    }
}
