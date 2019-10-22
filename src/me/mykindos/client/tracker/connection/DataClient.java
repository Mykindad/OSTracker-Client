package me.mykindos.client.tracker.connection;


import java.io.IOException;
import java.net.Socket;

public class DataClient {

    private String host;
    private int port;
    private CommandThread commandThread;
    private Socket connection;

    /**
     * Socket wrapper for managing our connection to the server
     * @param host Server IP
     * @param port Server Port
     */
    public DataClient(String host, int port) {
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
        if(connection != null && connection.isConnected()){
            throw new IOException("Already connected to server: " + getHost());
        }

        connection = new Socket(getHost(), getPort());
        connection.setKeepAlive(true);

        commandThread = new CommandThread(connection);
        commandThread.start();
    }

    public void disconnect() {
            if(commandThread != null){
                commandThread.interrupt();
            }
    }


}
