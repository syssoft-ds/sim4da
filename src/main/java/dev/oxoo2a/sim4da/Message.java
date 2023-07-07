package dev.oxoo2a.sim4da;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Message {

    private final String time;

    public Message () {
        this(new HashMap<>(), "");
    }
    protected Message ( HashMap<String,String> content ) {
        this(content, "");
    }
    public Message(Time time){
        this(new HashMap<>(), time.toString());
    }
    public Message(Message m, Time time){
        this(m.content, time);
    }
    public Message(HashMap<String,String> content, Time time){
        this(content, time.toString());
    }
    public Message(HashMap<String,String> content, String time){
        this.content = content;
        this.time = time;
    }

    public boolean hasTime(){
        return !time.equals("");
    }

    public String getTime(){
        return time;
    }

    public Message add ( String key, String value ) {
        content.put(key,value);
        return this;
    }

    public Message add ( String key, int value ) {
        content.put(key,String.valueOf(value));
        return this;
    }

    public String query ( String key ) {
        return content.get(key);
    }

    public Map<String,String> getMap () {
        return content;
    }

    public String toJson () {
        return serialize(this);
    }

    public static Message fromJson ( String s ) {
        return serializer.fromJson(s, Message.class);
    }

    private static synchronized String serialize ( Message m ) {
        return serializer.toJson(m); // Not sure about thread safety of Gson
    }

    protected final HashMap<String,String> content;
    protected static final Gson serializer = new Gson();
}
