package dev.oxoo2a.sim4da.clock;

/*TODO: Klasse in ABstrakt ändern. Gemeinsame Methode "synchronize(String) mit Tokenizer findet alle ID - Timestamp Paare"
*  Über ClockType Kondition verrechnen
*/

public abstract class LogicClock {



    public ClockType getType(){

    }
    public void tick(){

    }
    public void synchronize(String timeStamp){}
    public int getTime(){

    }

}
