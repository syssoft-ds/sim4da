package dev.oxoo2a.sim4da;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

public class MessageTest {
	@BeforeAll
	public static void createMessage () {
		m = new Message().add("a","b").add("c","d");
	}

	private static Message m;

	@Test
	public void testMessageBasics () {
		assertEquals(m.query("a"), "b");
		assertEquals(m.query("c"), "d");
		assertNull(m.query("no_key"));
	}

	@Test
	public void serializeAndDeserialize () {
		String m_json = m.toJson();
		Message m2 = Message.fromJson(m_json);
		assertEquals(m2.query("a"),"b");
		assertEquals(m2.query("c"),"d");
	}
}