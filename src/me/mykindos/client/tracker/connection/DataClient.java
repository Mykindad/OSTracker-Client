package me.mykindos.client.tracker.connection;


import java.io.IOException;
import java.net.Socket;

public class DataClient {

    private String host;
    private int port;

    private Socket connection;

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
        if(connection.isConnected()){
            throw new IOException("Already connected to server: " + getHost());
        }

        connection = new Socket(getHost(), getPort());
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
