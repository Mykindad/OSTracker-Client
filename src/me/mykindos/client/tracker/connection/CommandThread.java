package me.mykindos.client.tracker.connection;

import me.mykindos.client.tracker.command.CommandFactory;

import java.io.*;
import java.net.Socket;

/**
 * Thread for processing all commands for a client
 */
public class CommandThread extends Thread {

    private Socket connection;

    /**
     * Create thread for client socket
     * @param connection Connected client
     */
    public CommandThread(Socket connection) {
        super();
        this.connection = connection;
    }

    /**
     * While the connection is open, process all commands that get added to the commands queue
     */
    @Override
    public void run() {
        OutputStream outStream = null;
        PrintWriter outWriter = null;
        try {
            outStream = connection.getOutputStream();
            outWriter = new PrintWriter(outStream, true);


            while (!Thread.interrupted()) {
                String command = CommandFactory.getInstance().commands.poll();

                if (command != null && !command.equals("")) {
                    outWriter.println(command);
                }
            }

            outWriter.print("END");
            System.out.println("memess");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outWriter != null) {
                    outWriter.close();
                }
                if (outStream != null) {
                    outStream.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
