package dev.oxoo2a.sim4da;

import dev.oxoo2a.sim4da.Simulator.TimestampType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LeLannTest {
	
	private static final int NUMBER_OF_NODES = 3;
	private static final int DURATION = 2;
	
	@Test
	public void runLeLannSimulation() {
		Simulator s = new Simulator(NUMBER_OF_NODES, TimestampType.EXTENDED_LAMPORT, "LeLann", true, true, System.out);
		for (int id = 0; id<NUMBER_OF_NODES; id++) {
			Node n = new LeLannNode(s, id);
			s.attachNode(n);
		}
		try {
			s.runSimulation(DURATION);
		} catch (InstantiationException ignored) {
			Assertions.fail("Not all nodes instantiated");
		}
	}
	
	private static class LeLannNode extends Node {
		private LeLannNode(Simulator s, int id) {
			super(s, id);
		}
		@Override
		public void run() {
			//TODO
		}
	}
}
