package dev.oxoo2a.sim4da;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Message {

    public Message () {
        content = new HashMap<>();
    }

    protected Message ( HashMap<String,Object> content ) {
        this.content = content;
    }
    public Message add ( String key, Object value ) {
        content.put(key,value);
        return this;
    }

    public Message add ( String key, int value ) {
        content.put(key,String.valueOf(value));
        return this;

    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String query ( String key ) {
       Object value = content.get(key);
       return (value != null) ? value.toString() : null;
    }

    public Map<String,Object> getMap () {
        return content;
    }

    public String toJson () {
        return serialize(content);
    }

    public static Message fromJson ( String s ) {
        Type contentType = new TypeToken<HashMap<String,Object>>() {}.getType();
        return new Message(serializer.fromJson(s,contentType));
    }

    private static synchronized String serialize ( Map<String,Object> content ) {
        return serializer.toJson(content); // Not sure about thread safety of Gson
    }

    private final HashMap<String,Object> content;
    private int timestamp;
    private static final Gson serializer = new Gson();



}
