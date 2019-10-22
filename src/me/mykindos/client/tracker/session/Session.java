package me.mykindos.client.tracker.session;

/**
 * Object for storing all data in the current period.
 * When a new interval starts, the session is reset and all previous data is uploaded to the server
 */
public class Session {

    private long startTime;
    private long runTime;

    /**
     * Create a session with the current system time
     */
    public Session(){
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Create Session with a start time in MS
     * @param startTime
     */
    public Session(long startTime){
        this.startTime = startTime;
    }


    /**
     * @return Session start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @return Current run time of this session
     */
    public long getRunTime() {
        return runTime;
    }

    /**
     * Sets the time commenced since the session started.
     * @param runTime Unix timestamp
     */
    public void setRunTime(long runTime){
        this.runTime = runTime - startTime;
    }


}
