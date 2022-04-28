package dev.oxoo2a.sim4da;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MessageTest {

	@Test
	public void testMessageBasics () {
		Message m = new Message().add("a","b");
		assertEquals(m.query("a"), "c");
	}
}