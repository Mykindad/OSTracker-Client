package me.mykindos.client.tracker.command;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Singleton class for adding commands to the execution queue
 */
public class CommandFactory {

    public ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
    private static CommandFactory commandFactory;

    /**
     * Only one instance of CommandFactory to prevent commands being processed multiple times
     */
    private CommandFactory(){
    }

    /**
     * @param command Queues a command to be sent to the server
     */
    public void runCommand(String command) {
        if(command.equals("")) return;
       commands.add(command);
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
