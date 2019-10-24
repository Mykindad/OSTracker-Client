package me.mykindos.client.tracker.session;

import me.mykindos.client.tracker.Tracker;
import me.mykindos.client.tracker.command.CommandExecutor;
import me.mykindos.client.tracker.command.CommandFactory;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Manages the session
 * EXP, Item, and Time Tracking
 */
public class SessionTracker {

    private Session session;
    private HashMap<Skill, Integer> expPerSkill;
    private Tracker tracker;
    private TrackerThread trackerThread;

    /**
     * Starts the SessionTracker
     *
     * @param tracker Tracker
     */
    public SessionTracker(Tracker tracker) {
        this.tracker = tracker;
        this.expPerSkill = new HashMap<>();
        session = new Session();
        Arrays.stream(Skill.values()).forEach(s -> {
            expPerSkill.put(s, 0);
        });

        trackerThread = new TrackerThread(this);
        trackerThread.start();
    }


    /**
     * Ends the current tracking session, prepares data for transfer to server
     */
    public void endSession() {
        try {
            tracker.getServerConnection().connect();

            Arrays.stream(Skill.values()).forEach(s -> {
                System.out.println("Put " + s.name() + ", " + (tracker.getExperienceTracker().getGainedXP(s) - expPerSkill.get(s)));
                session.getExpGained().put(s, tracker.getExperienceTracker().getGainedXP(s) - expPerSkill.get(s));
                expPerSkill.put(s, tracker.getExperienceTracker().getGainedXP(s));
            });

            session.setRunTime(System.currentTimeMillis() - session.getStartTime());
            CommandExecutor.updateSession(tracker.getScriptName(), tracker.getClient().getUsername(), session);
            CommandFactory.getInstance().runCommand("END");

        } catch (IOException e) {
            System.out.println("Failed to connect to server... data still being tracked incase connection reestablishes");
            tracker.mysqlConnected = false;
            e.printStackTrace();
        } finally {
            session = new Session();

        }
    }

    public Tracker getTracker() {
        return tracker;
    }

    public Session getSession() {
        return session;
    }
}
