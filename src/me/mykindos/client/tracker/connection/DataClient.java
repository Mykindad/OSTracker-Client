package me.mykindos.client.tracker.connection;


import me.mykindos.client.tracker.Tracker;
import me.mykindos.client.tracker.exceptions.InvalidSetupException;

import java.io.IOException;
import java.net.Socket;

public class DataClient {

    private Tracker tracker;
    private String host;
    private int port;
    private CommandThread commandThread;
    private Socket connection;

    /**
     * Socket wrapper for managing our connection to the server
     *
     * @param tracker Tracker instance
     * @param host Server IP
     * @param port Server Port
     */
    public DataClient(Tracker tracker, String host, int port) {
        this.tracker = tracker;
        this.host = host;
        this.port = port;
    }

    /**
     * @return Server IP
     */
    public String getHost() {
        return host;
    }

    /**
     * @return Server port
     */
    public int getPort() {
        return port;
    }

    public Socket getConnection() {
        return connection;
    }

    /**
     * Establish a connection with the server
     * Will force close any existing connections if they remain open. (Server will terminate it on the server side anyway)
     * @throws IOException
     */
    public void connect() throws IOException {

        if (connection != null ) {
            commandThread.interrupt();
        }

        connection = new Socket(getHost(), getPort());

        commandThread = new CommandThread(connection);
        commandThread.start();

        if(!tracker.mysqlConnected && tracker.isRunning){
            try {
                tracker.setupMysql(tracker.mysqlHost, tracker.mysqlUsername, tracker.mysqlPassword);
            } catch (InvalidSetupException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Interrupt the commandThread, which will then close the connection
     */
    public void disconnect() {
        if (commandThread != null) {
            commandThread.interrupt();
        }
    }

    /**
     * @return Tracker
     */
    public Tracker getTracker(){
        return tracker;
    }


}
