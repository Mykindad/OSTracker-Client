package me.mykindos.client.tracker;

import me.mykindos.client.tracker.command.CommandExecutor;
import me.mykindos.client.tracker.connection.DataClient;
import me.mykindos.client.tracker.exceptions.InvalidSetupException;
import me.mykindos.client.tracker.session.Session;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

import java.io.IOException;

/**
 * Base of entire tracking system
 */
public class Tracker extends MethodProvider {


    private DataClient serverConnection;
    private double updateInterval = 60;
    private boolean mysqlConnected = false;
    private String scriptName;
    private boolean enabled = true;
    private Session session;

    /**
     * Creates an instance of the Tracker with bot context
     * @param bot Bot client
     */
    public Tracker(Bot bot, String scriptName){
        exchangeContext(bot);
        this.scriptName = scriptName;
        this.session = new Session(System.currentTimeMillis());
    }

    /**
     * @return DataClient Instance
     */
    public DataClient getServerConnection(){
        return serverConnection;
    }

    /**
     * Establish a connection with the server
     * @param host IP of the server
     * @param port Port of the server
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker establishConnection(String host, int port) throws InvalidSetupException {
        if(serverConnection != null){
            throw new InvalidSetupException("Connection to server has already been established.");
        }

        try {
            serverConnection.connect();
        } catch (IOException e) {
            enabled = false;
            log("Failed to connect to server. Tracker disabled");
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Instruct the server to connect to the provided MySQL Server
     * @param host IP of the MySQL Server
     * @param username MySQL username
     * @param password MySQL Password
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker setupMysql(String host, String username, String password) throws InvalidSetupException {
        if(serverConnection == null){
            throw new InvalidSetupException("You must have an active connection to call this function");
        }

        CommandExecutor.connectMySQL(host, username, password);
        CommandExecutor.createDatabase(getScriptName());

        return this;
    }

    /**
     * Optional
     * Creates a user on the provided MySQL connection
     * @param username Username
     * @param password Password
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker createMySQLUser(String username, String password) throws InvalidSetupException {
        if(serverConnection == null){
            throw new InvalidSetupException("You must have an active connection to call this function");
        }

        CommandExecutor.createMySQLUser(username, password);

        return this;
    }

    /**
     * Set the time in minutes between each update to the server
     * @param minutes Minutes between each update
     * @return Tracker
     */
    public Tracker setUpdateInterval(double minutes){
        updateInterval = minutes;
        return this;
    }

    /**
     * Starts the tracker
     * Begins tracking:
     * - Exp Gained for all Skills
     * - Time Played
     * - Items gained / lost
     *
     * Requires a connection to the server
     *
     * @return Tracker
     * @throws InvalidSetupException
     */
    public Tracker start() throws InvalidSetupException {
        if(serverConnection == null){
            throw new InvalidSetupException("You must have an active connection before starting the tracker. Call establishConnection");
        }

        getServerConnection().disconnect(); // Only need an open connection when we are submitting data

        return this;
    }

    /**
     * Terminates the Tracker, uploads contents of current session
     */
    public void stop(){
        if(serverConnection != null){
            if(serverConnection.getConnection().isConnected()){
                serverConnection.disconnect();
            }
        }
    }

    /**
     * @return The name of the script
     */
    public String getScriptName() {
        return scriptName;
    }
}
