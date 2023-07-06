package dev.oxoo2a.sim4da;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DSSimulation {
    private static final int NUM_ACTORS = 4;
    private static final int MAX_STEPS = 100;

    public static void main(String[] args) {
        List<Actor> actors = new ArrayList<>();
        for (int i = 0; i < NUM_ACTORS; i++) {
            actors.add(new Actor(NUM_ACTORS));
        }

        for (int i = 0; i < actors.size(); i++) {
            actors.get(i).isActive(i); // Prints initial status of each actor with index
        }

        int time = 1;
        int steps = 0;

        DoubleCountingActor doubleCountingActor = new DoubleCountingActor(NUM_ACTORS);
        LaggingStateVector vectorClockActor = new LaggingStateVector(NUM_ACTORS);

        while (steps < MAX_STEPS) {
            System.out.println("------------------------------");
            System.out.println("Step " + steps + " starting...");
            System.out.println("------------------------------");

            List<Actor> activeActors = new ArrayList<>();
            for (int i = 0; i < actors.size(); i++) {
                Actor actor = actors.get(i);
                actor.updateP(time);
                if (actor.isActive(i)) {
                    activeActors.add(actor);
                }
            }

            boolean anyMessagesSent = false;
            for (Actor actor : activeActors) {
                int senderIndex = actors.indexOf(actor);
                if (actor.sendMessage(actors, senderIndex)) {
                    anyMessagesSent = true;
                    doubleCountingActor.receiveMessage(senderIndex);
                }
            }

            doubleCountingActor.actorTerminated(activeActors.size());

            int[] senderVectorClock = new int[NUM_ACTORS];
            vectorClockActor.receiveMessage(activeActors, steps % NUM_ACTORS, senderVectorClock);

            System.out.println("Step " + steps + ": Active actors: " + activeActors.size());
            System.out.println("Event Clock: " + Arrays.toString(vectorClockActor.getVectorClock()));

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
