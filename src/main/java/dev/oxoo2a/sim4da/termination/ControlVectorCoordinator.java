package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ControlVectorCoordinator extends Node {

    public static Map<Integer, Integer[]> clientVectors = new HashMap<>();
    boolean prompt = true;
    boolean finished = false;


    public ControlVectorCoordinator(int my_id) {

        super(my_id);
    }

    @Override
    protected void main() {
        while (true) {
            Message m;
            //send round message
            if(prompt){
                prompt=false;
                m=new Message();
                String vectorString="";
                for (int i = 0; i < Main.n_nodes-1; i++) {
                    String subString = i+":"+0+";";
                    vectorString = vectorString+subString;
                }
                vectorString = vectorString + (Main.n_nodes-1)+ ":" + 0;
                System.out.println("VectorString");
                System.out.println(vectorString);
                m.add("type" , "control_vector");
                m.add("vector",  vectorString);
                sendUnicast(0,m);
            }

            //wait for response
            Network.Message m_raw = receive();
            if(m_raw!=null){
                System.out.println("Vector arrived back at COORDINATOR");
                System.out.println(m_raw);
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
                        System.out.println("FOUND VALUE NOT 0 -> NOT FINISHED");
                        break;
                    }
                }
                System.out.println("Finished is " + finished);
                if(finished){
                    System.out.println("CONTROL VECTOR SAYS SIMULATION DONE");
                    System.exit(0);
                }else{
                   prompt = true;
                }

            }






        }

    }


}
