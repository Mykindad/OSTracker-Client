package me.mykindos.client.tracker;

import me.mykindos.client.tracker.command.CommandExecutor;
import me.mykindos.client.tracker.command.CommandFactory;
import me.mykindos.client.tracker.connection.DataClient;
import me.mykindos.client.tracker.exceptions.InvalidSetupException;
import me.mykindos.client.tracker.session.Session;
import me.mykindos.client.tracker.session.SessionTracker;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.io.IOException;

/**
 * Base of entire tracking system
 */
public class Tracker extends MethodProvider {


    public boolean isRunning = false;
    private DataClient serverConnection;
    private double updateInterval = 60;
    private String scriptName;
    private SessionTracker sessionTracker;

    public boolean mysqlConnected = false;
    public String mysqlHost;
    public String mysqlUsername;
    public String mysqlPassword;

    /**
     * Creates an instance of the Tracker with bot context
     *
     * @param bot Bot client
     */
    public Tracker(Bot bot, String scriptName) {
        exchangeContext(bot);
        this.scriptName = scriptName;
    }

    /**
     * @return DataClient Instance
     */
    public DataClient getServerConnection() {
        return serverConnection;
    }

    /**
     * Establish a connection with the server
     *
     * @param host IP of the server
     * @param port Port of the server
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker establishConnection(String host, int port) throws InvalidSetupException {
        if (serverConnection != null) {
            throw new InvalidSetupException("Connection to server has already been established.");
        }

        try {
            serverConnection = new DataClient(this, host, port);
            serverConnection.connect();
        } catch (IOException e) {
            isRunning = false;
            log("Failed to connect to server. Tracker disabled");
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Instruct the server to connect to the provided MySQL Server
     *
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker setupMysql() throws InvalidSetupException {
        if (serverConnection == null) {
            throw new InvalidSetupException("You must have an active connection to call this function");
        }

        CommandExecutor.connectMySQL();
        CommandExecutor.createDatabase(getScriptName());


        mysqlConnected = true;

        return this;
    }


    /**
     * Starts the tracker
     * Begins tracking:
     * - Exp Gained for all Skills
     * - Time Played
     * - Items gained / lost
     * <p>
     * Requires a connection to the server
     *
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker start() throws InvalidSetupException {
        if (serverConnection == null) {
            throw new InvalidSetupException("You must have an active connection before starting the tracker. Call establishConnection");
        }
        CommandFactory.getInstance().runCommand("AddUser;;"+  getScriptName() + "--" + getClient().getUsername());
        CommandFactory.getInstance().runCommand("END");

        getExperienceTracker().startAll();
        sessionTracker = new SessionTracker(this);
        isRunning = true;
        return this;
    }

    /**
     * Terminates the Tracker, uploads contents of current session
     */
    public void stop() {
        try {
            isRunning = false;
            sessionTracker.endSession();
            log("Tracker stopping... uploading data");
            Script.sleep(1000); // Allow time for it to all transfer before cutting the connection
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getServerConnection() != null) {
            if (getServerConnection().getConnection().isConnected()) {
                getServerConnection().disconnect();
            }
        }


    }

    /**
     * @return The name of the script
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @return True if tracker is currently running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Length of time data is tracked for before uploading
     * @return Time in minutes between each 'session' interval
     */
    public double getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Set the time in minutes between each update to the server
     *
     * @param minutes Minutes between each update
     * @return Tracker
     */
    public Tracker setUpdateInterval(double minutes) {
        updateInterval = minutes;
        return this;
    }
}
