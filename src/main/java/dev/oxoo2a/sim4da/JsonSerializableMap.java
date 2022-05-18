package dev.oxoo2a.sim4da;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonSerializableMap extends HashMap<String, String> {
    
    private static final Gson SERIALIZER = new Gson();
    
    private static synchronized String serialize(Map<String, String> content) {
        return SERIALIZER.toJson(content); // Not sure about thread safety of Gson
    }
    
    public String toJson() {
        return serialize(this);
    }
    
    public static JsonSerializableMap fromJson(String s) {
        Type contentType = new TypeToken<JsonSerializableMap>() {}.getType();
        return SERIALIZER.fromJson(s, contentType);
    }
}
