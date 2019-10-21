package me.mykindos.client.tracker.command;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandFactory {

    public static ConcurrentLinkedQueue<String> queries = new ConcurrentLinkedQueue<>();
    private static CommandFactory commandFactory;
    private static Thread commandThread;

    /**
     * Only one instance of CommandFactory to prevent commands being processed multiple times
     */
    private CommandFactory(){

    }

    /**
     * @param command Queues a command to be sent to the server
     */
    public void runCommand(String command) {
        queries.add(command);
    }

    /**
     * Instance of QueryFactory
     *
     * @return QueryFactory
     */
    public static CommandFactory getInstance() {
        if (commandFactory == null) {
            commandFactory = new CommandFactory();
        }

        return commandFactory;
    }
}
