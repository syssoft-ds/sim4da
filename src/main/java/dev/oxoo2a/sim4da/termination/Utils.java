package dev.oxoo2a.sim4da.termination;

import java.util.StringTokenizer;

public class Utils {

    public static int [] getVectorFromString(String vectorAsString, int numberOfNodes){

        StringTokenizer tokenizer= new StringTokenizer(vectorAsString, ";");
        if(tokenizer.countTokens()> numberOfNodes)
            throw new IllegalStateException("The number of Values is longer than the number of nodes");
        int [] controlVector= new int[numberOfNodes];
        int iterator= 0;
        while(tokenizer.hasMoreTokens()){
            String token= tokenizer.nextToken();
            if(!token.isBlank()) {
                String[] valueAsString = token.split(":");
                if(valueAsString.length>2)
                    throw new IllegalStateException("if there are more than 2 tokens than it is not +/- on first and the value on second");
                int value = Integer.parseInt(valueAsString[1]);
                if (valueAsString[0].equals("-")) {
                    value *= -1;
                }
                controlVector[iterator] = value;
                iterator++;
            }
        }
        return controlVector;
    }


}
