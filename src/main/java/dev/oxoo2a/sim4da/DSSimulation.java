package dev.oxoo2a.sim4da;

import java.util.ArrayList;
import java.util.List;

public class DSSimulation {
    private static final int NUM_ACTORS = 10;
    private static final int MAX_STEPS = 100;

    public static void main(String[] args) {
        List<Actor> actors = new ArrayList<>();
        for (int i = 0; i < NUM_ACTORS; i++) {
            actors.add(new Actor());
        }

        int time = 1;
        int steps = 0;

        DoubleCountingActor doubleCountingActor = new DoubleCountingActor();
        LaggingStateVector vectorClockActor = new LaggingStateVector(NUM_ACTORS);

        while (steps < MAX_STEPS) {
            List<Actor> activeActors = new ArrayList<>();
            for (Actor actor : actors) {
                actor.updateP(time);
                if (actor.isActive()) {
                    activeActors.add(actor);
                }
            }

            boolean anyMessagesSent = false;
            for (Actor actor : activeActors) {
                if (actor.sendMessage(actors)) {
                    anyMessagesSent = true;
                    int senderId = actors.indexOf(actor);
                    doubleCountingActor.receiveMessage(senderId);
                }
            }

            doubleCountingActor.actorTerminated(activeActors.size());

            int[] senderVectorClock = new int[NUM_ACTORS];
            vectorClockActor.receiveMessage(activeActors, steps % NUM_ACTORS, senderVectorClock);

            System.out.println("Step " + steps + ": Active actors: " + activeActors.size());

            if (!anyMessagesSent) {
                break;
            }

            steps++;
            time++;
        }

        boolean doubleCountingTerminated = doubleCountingActor.hasTerminated();
        boolean vectorClockTerminated = vectorClockActor.hasTerminated();

        if (doubleCountingTerminated) {
            System.out.println("Double Counting Termination: All actors have terminated!");
        } else {
            System.out.println("Double Counting Termination: Not all actors have terminated yet.");
        }

        if (vectorClockTerminated) {
            System.out.println("Lagging State Vector Termination: All actors have terminated!");
        } else {
            System.out.println("Lagging State Vector Termination: Not all actors have terminated yet.");
        }
    }
}
