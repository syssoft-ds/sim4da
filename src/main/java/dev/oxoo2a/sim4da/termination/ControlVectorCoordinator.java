package dev.oxoo2a.sim4da.termination;

import dev.oxoo2a.sim4da.Message;
import dev.oxoo2a.sim4da.Network;
import dev.oxoo2a.sim4da.Node;

import java.util.StringTokenizer;

/**
 * Control Vector Coodinator:
 * Schickt (wenn der boolean 'prompt' wahr ist), eine Nachricht mit einem Nullvektor an den ersten Basisaktor und
 * wartet dann auf eine Antwort. Jeder Basisaktor schickt diese Vektornachricht an den nächsten Basisaktor, bis der
 * letzte die Nachricht wieder zurück an diesen Coordinator schickt. Erst dann wird geprüft, ob der Vektor, der
 * zurückgekommen ist, ein Nullvektor ist. Ansonsten wird 'prompt' wieder auf wahr gesetzt und eine  neue Nachricht
 * wird losgeschickt und macht die Runde. VectorStrings in den Nachrichten entsprechen der Form
 * "id1:value1;id2:value2;id3:value3;..." und können so einfach mit einem Tokenizer ausgelesen werden.
 *
 * Anmerkung: Mir ist im Nachhinein aufgefallen, dass ich das Controlvektor Verfahren nicht genau wie in der Vorlesung
 * beschrieben umgesetzt hab. In der VL behält der Controlvector durchlaufübergreifend seine Einträge und die lokalen
 * Vektoren der Basisaktoren werden auf 0 gesetzt, und somit immer vom Controlvector "eingesammelt". In meiner Implementierung
 * behalten die Basisaktoren ihre lokalen Vektoren über den ganzen Simulationsverlauf, der Controlvector beginnt allerdings
 * in jedem neuen Durchlauf immer als 0-Vektor. Das Verfahren ist im Ergebnis also Äquivalent.
 */
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
                /**
                 * Hier kommt nur eine Nachricht an, wenn sie zuvor bei allen Basisnodes gewesen ist. In jeder Basisnode wird
                 * der Vektor mit dem lokalen Vektor verrechnet und weitergeschickt. Wenn hier wieder der Nullvektor zurückkommt
                 * ist das System terminiert.
                 */

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
