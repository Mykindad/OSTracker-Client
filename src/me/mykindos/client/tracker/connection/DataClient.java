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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Socket getConnection() {
        return connection;
    }

    public void connect() throws IOException {

        if (connection != null ) {
            connection.close();
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

    public void disconnect() {
        if (commandThread != null) {
            commandThread.interrupt();
        }
    }

    public Tracker getTracker(){
        return tracker;
    }


}
