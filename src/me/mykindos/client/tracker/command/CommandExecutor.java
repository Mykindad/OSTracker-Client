package me.mykindos.client.tracker.command;

import me.mykindos.client.tracker.session.Session;

/**
 * Handles the sending of commands to the server
 */
public class CommandExecutor {

    /**
     * Send all data from the current session to the server
     * @param session Current session, determined by interval
     */
    public static void updateSession(Session session){

    }


    /**
     * Combines all exp gains into one command
     * @param session
     * @return
     */
    private static String generateExperienceGainedCommand(Session session){
        String command = "AddExperience;;";

        return command;
    }

    /**
     * Send instruction to the server to create a MySQL Database with the specified name.
     * Will not run if the database already exists
     * @param scriptName Name of script
     */
    public static void createDatabase(String scriptName){
        String command = "CreateDatabase;;osbot-" + scriptName;
        CommandFactory.getInstance().runCommand(command);
    }

    /**
     * Send instruction to the server to create a MySQL user if it doesnt already exist
     * Will grant the user full access to any database starting with osbot-
     * @param username User username
     * @param password User password
     */
    public static void createMySQLUser(String username, String password){
        String create = "CreateUser;;" + username + "--" + password;
        String grant = "GrantPrivileges;;" + username;
        CommandFactory.getInstance().runCommand(create);
        CommandFactory.getInstance().runCommand(grant);
    }

    /**
     * Instruct server to connect to the provided MySQL Server
     * @param host IP of MySQL Server
     * @param username MySQL username
     * @param password MySQL password
     */
    public static void connectMySQL(String host, String username, String password){
        String command = "ConnectMySQL;;" + host + "--" +username + "--" + password;
        CommandFactory.getInstance().runCommand(command);
    }


}
