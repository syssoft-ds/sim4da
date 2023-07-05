package dev.oxoo2a.sim4da;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MessageWithTime extends Message{

    private final String time;

    public MessageWithTime(Time time){
        super();
        this.time = time.toString();
    }
    public MessageWithTime(HashMap<String,String> content, Time time){
        super(content);
        this.time = time.toString();
    }
    public MessageWithTime(Message m, Time time){
        this(m.content, time);
    }

    public String getTime(){return time;}

    public String toJson () {
        return serialize(this);
    }

    public static MessageWithTime fromJson ( String s ) {
        //Type contentType = new TypeToken<HashMap<String,String>>() {}.getType();
        //return new Message(serializer.fromJson(s,contentType));
        return serializer.fromJson(s, MessageWithTime.class);
    }

    private static synchronized String serialize ( MessageWithTime m ) {
        //HashMap<Map<String, String>, >
        String s = serializer.toJson(m); // Not sure about thread safety of Gson
        return s;
    }


}
