package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

public class MessageTest {
    
    private static final JsonSerializableMap m = new JsonSerializableMap();
    
    @BeforeAll
    public static void createMessage () {
        m.put("a", "b");
        m.put("c", "d");
    }
    
    @Test
    public void testMessageBasics () {
        Assertions.assertEquals(m.get("a"), "b");
        Assertions.assertEquals(m.get("c"), "d");
        Assertions.assertNull(m.get("no_key"));
    }
    
    @Test
    public void serializeAndDeserialize () {
        String m_json = m.toJson();
        JsonSerializableMap m2 = JsonSerializableMap.fromJson(m_json);
        Assertions.assertEquals(m2.get("a"), "b");
        Assertions.assertEquals(m2.get("c"), "d");
    }
}
