package dev.oxoo2a.sim4da;

public interface Time {
    /***
     * Increment the time of the local node
     */
    void incrementMyTime();

    /***
     * String representation of the time
     * @return - time as string
     */
    String toString();

    /***
     * New time was received by a different node (sender).
     * Now the time of the local node must be updated.
     * @param time_sender - time of the sender node in the string representation
     */
    void updateTime(String time_sender);
}
