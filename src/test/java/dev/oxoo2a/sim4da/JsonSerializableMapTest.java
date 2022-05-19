package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

public class JsonSerializableMapTest {
    
    private static final JsonSerializableMap m = new JsonSerializableMap();
    
    @BeforeAll
    public static void createMap() {
        m.put("a", "b");
        m.put("c", "d");
    }
    
    @Test
    public void testMapBasics() {
        Assertions.assertEquals(m.get("a"), "b");
        Assertions.assertEquals(m.get("c"), "d");
        Assertions.assertNull(m.get("no_key"));
    }
    
    @Test
    public void serializeAndDeserializeMap() {
        String jsonString = m.toJson();
        JsonSerializableMap m2 = JsonSerializableMap.fromJson(jsonString);
        Assertions.assertEquals(m2.get("a"), "b");
        Assertions.assertEquals(m2.get("c"), "d");
    }
}
