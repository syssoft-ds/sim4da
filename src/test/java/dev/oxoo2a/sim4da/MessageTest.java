package dev.oxoo2a.sim4da;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {
    
    private static final Message m = new Message().add("a","b").add("c","d");
    
    @Test
    public void testMessageBasics () {
        Assertions.assertEquals(m.query("a"), "b");
        Assertions.assertEquals(m.query("c"), "d");
        Assertions.assertNull(m.query("no_key"));
    }
    
    @Test
    public void serializeAndDeserialize () {
        String m_json = m.toJson();
        Message m2 = Message.fromJson(m_json);
        Assertions.assertEquals(m2.query("a"),"b");
        Assertions.assertEquals(m2.query("c"),"d");
    }
}
