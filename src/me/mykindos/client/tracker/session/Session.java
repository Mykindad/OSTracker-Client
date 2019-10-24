package me.mykindos.client.tracker.session;

import org.osbot.rs07.api.ui.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Object for storing all data in the current period.
 * When a new interval starts, the session is reset and all previous data is uploaded to the server
 */
public class Session {


    private HashMap<Skill, Integer> expGained;
    private List<ItemData> itemData;
    private long startTime;
    private long runTime;

    /**
     * Create a session with the current system time
     */
    public Session(){
        this.startTime = System.currentTimeMillis();
        expGained = new HashMap<>();
        itemData = new ArrayList<>();
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
        this.runTime = runTime;
    }

    /**
     * Hashmap of exp gains for each skill this session
     * @return HashMap of exp gains
     */
    public HashMap<Skill, Integer> getExpGained(){
        return expGained;
    }

    /**
     * @return List of Items that have been Gained / Lost / Spent
     */
    public List<ItemData> getItemData() {
        return itemData;
    }
}
