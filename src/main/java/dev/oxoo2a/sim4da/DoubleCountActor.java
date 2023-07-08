package dev.oxoo2a.sim4da;

public class DoubleCountActor extends CountActor {

    final int TERMINATION_CHECK_SEC = 3; // check termination every n seconds
    private long startTime;


    private int totalSent = 0;
    private int totalReceived = 0;
    private int controlCount = 0;

    private boolean isFetchingControl = false;
    private boolean allPassive = false;
    private boolean isFirstCheckTerminated = false;
    private boolean terminated = false;
    public DoubleCountActor(int my_id) {
        super(my_id, false);
    }
    public DoubleCountActor( int my_id, Clock clock ) { super(my_id, clock);}

    @Override
    protected void main() {
        startTime = System.currentTimeMillis();

        Message control_cast = new Message().add("Sender",myId).add("isControl", "true");

        Thread threadCheck = new Thread(() -> {
            while(true) {
                if (terminated)
                    break;

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (!isFetchingControl) {
                    if (elapsedTime >= TERMINATION_CHECK_SEC * 1000) {
                        System.out.println("Double Count Actor: Check termination");
                        startTime = System.currentTimeMillis();
                        isFetchingControl = true;
                        allPassive = true;
                        multiCast(control_cast);
                    }
                }
            }
        });

        Thread threadCount = new Thread(() -> {
            while(true) {
                // receiving
                Network.Message m_raw = receive();
                if (m_raw == null) break; // Null == Node2Simulator time ends while waiting for a message

                Message m_json = Message.fromJson(m_raw.payload);
                boolean isControl = Boolean.parseBoolean(m_json.query("isControl"));
                int sender = Integer.parseInt(m_json.query("Sender"));

                if (!isControl) {
                    setReceived(getReceived() + 1);
                    continue;
                }
                boolean isActive = Boolean.parseBoolean(m_json.query("isActive"));
                int sent = Integer.parseInt(m_json.query("sent"));
                int received = Integer.parseInt(m_json.query("received"));

                totalSent += sent;
                totalReceived += received;
                controlCount++;

                if (isActive)
                    allPassive = false;

                if (controlCount == numberOfNodes()-1){
                    System.out.println(getReceived());
                    System.out.println(getSent());

                    totalSent += getSent();
                    totalReceived += getReceived();
                    if (isActive())
                        allPassive = false;


                    if (!allPassive)
                        System.out.println("Double Count Actor: Not all actors are passive.");
                    else
                        System.out.printf("Double Count Actor:  sent #%d, received #%d \n", totalSent, totalReceived);

                    if (totalReceived == totalSent && allPassive) {
                        if (isFirstCheckTerminated){
                            terminated = true;
                            System.out.println("Double Count Actor: Terminated.");
                            break;
                        }else {
                            isFirstCheckTerminated = true;
                            startTime = System.currentTimeMillis();
                            System.out.println("Double Count Actor: Starting second check.");
                        }
                    }else {
                        isFirstCheckTerminated = false;
                        startTime = System.currentTimeMillis();
                        System.out.println("Double Count Actor: Termination check failed. Check again.");
                    }
                    isFetchingControl = false;
                    controlCount = 0;
                    totalSent = 0;
                    totalReceived = 0;
                    allPassive = true;
                }

            }
        });

        threadCheck.start();
        threadCount.start();

        super.main();
        threadCheck.interrupt();
        threadCount.interrupt();
    }

    private void multiCast(Message control_cast){
        for (int i = 0; i < numberOfNodes(); i++) {
            if (i == myId)
                continue;
            sendUnicast(i,control_cast);
        }
    }
}
