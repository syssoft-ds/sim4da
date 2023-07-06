package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Node;

import java.util.HashMap;
import java.util.Map;

public class ControlVectorCoordinator extends Node {

    public static Map<Integer, Integer[]> clientVectors = new HashMap<>();
    boolean prompt = true;


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

            //check if vector came back as zero




        }

    }


}
