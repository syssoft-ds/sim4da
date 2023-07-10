package dev.oxoo2a.sim4da;

public class VectorControlActor extends VectorActor {

    final int TERMINATION_CHECK_SEC = 3; // check termination every n seconds
    private long startTime;
    private int controlCount = 0;

    private int[] controlVector;
    private boolean isFetchingControl = false;
    private boolean isFirstCheckTerminated = false;
    private boolean allPassive = false;
    private boolean terminated = false;

    public VectorControlActor(int my_id, int n) {
        super(my_id, false, n);
        controlVector = new int[n];
    }

    public VectorControlActor(int my_id, Clock clock, int n) {
        super(my_id, clock, n);
        controlVector = new int[n];
    }

    @Override
    protected void main() {
        startTime = System.currentTimeMillis();
        Message control_cast = new Message().add("Sender", myId).add("isControl", "true");

        Thread threadCheck = new Thread(() -> {
            while (true) {
                if (terminated)
                    break;

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (!isFetchingControl) {
                    if (elapsedTime >= TERMINATION_CHECK_SEC * 1000) {
                        System.out.println("Vector Control Actor: Check termination");
                        startTime = System.currentTimeMillis();
                        isFetchingControl = true;
                        allPassive = true;
                        multiCast(control_cast);
                    }
                }
            }
        });

        Thread threadCount = new Thread(() -> {
            while (true) {
                // receiving
                Network.Message m_raw = receive();
                if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message

                Message m_json = Message.fromJson(m_raw.payload);
                boolean isControl = Boolean.parseBoolean(m_json.query("isControl"));

                if (!isControl) {
                    vector[myId] = vector[myId] - 1;
                    continue;
                }
                String vectorString = m_json.query("vector");
                int[] vectorSent = stringToVector(vectorString);

                controlVector = vectorAdd(controlVector, vectorSent);
                controlCount++;

                if (isActive())
                    allPassive = false;

                if (controlCount == numberOfNodes() - 1) {
                    controlVector = vectorAdd(controlVector, vector);

                    if (isActive())
                        allPassive = false;

                    if (!allPassive)
                        System.out.println("Vector Control Actor: Not all actors are passive.");
                    else
                        System.out.printf("Vector Control Actor: " + vectorToString(controlVector));

                    if (vectorIsZero(controlVector) && allPassive) {
                        if (isFirstCheckTerminated) {
                            terminated = true;
                            System.out.println("Vector Control Actor: Terminated.");
                            break;
                        } else {
                            isFirstCheckTerminated = true;
                            startTime = System.currentTimeMillis();
                            System.out.println("Vector Control Actor: Starting second check.");
                        }
                    } else {
                        isFirstCheckTerminated = false;
                        startTime = System.currentTimeMillis();
                        System.out.println("Vector Control Actor: Termination check failed. Check again.");
                    }
                    isFetchingControl = false;
                    controlVector = new int[controlVector.length];
                    controlCount = 0;
                    allPassive = true;
                }

            }
        });

        threadCheck.start();
        threadCount.start();

        //super.main();
        //threadCheck.interrupt();
        //threadCount.interrupt();
    }

    private boolean vectorIsZero(int[] vec) {
        for (int i = 0; i < vec.length; i++) {
            if (vec[i] != 0)
                return false;
        }
        return true;
    }

    private int[] vectorAdd(int[] vec, int[] vec2) {
        int[] newVec = new int[vec.length];

        for (int i = 0; i < vec.length; i++) {
            newVec[i] = vec[i] + vec2[i];
        }

        return newVec;
    }

    @Override
    protected void handleControlMessage(Message control_cast, int controlID) {

    }

    @Override
    protected void handleBasicMessage(Message basic_cast) {

    }

    private void multiCast(Message control_cast) {
        for (int i = 0; i < numberOfNodes(); i++) {
            if (i == myId)
                continue;
            sendUnicast(i, control_cast);
        }
    }
}

