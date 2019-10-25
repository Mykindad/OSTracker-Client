package me.mykindos.client.tracker.command;

import me.mykindos.client.tracker.session.ItemData;
import me.mykindos.client.tracker.session.Session;
import org.osbot.rs07.api.ui.Skill;

import java.util.Map;

/**
 * Handles the sending of commands to the server
 */
public class CommandExecutor {

    /**
     * Send all data from the current session to the server
     * @param scriptName Script name
     * @param username OSBot username
     * @param session Current session, determined by interval
     */
    public static void updateSession(String scriptName, String username, Session session){
        CommandFactory.getInstance().runCommand(generateAddItemCommand(scriptName, session));
        CommandFactory.getInstance().runCommand(generateRunTimeCommand(scriptName, username, session));
        CommandFactory.getInstance().runCommand(generateExperienceGainedCommand(scriptName, username, session));
        CommandFactory.getInstance().runCommand(generateScriptItemCommand(scriptName, username, session));
    }

    /**
     * Create RunTime command for the current session
     * @param session The session
     * @return Command to run
     */
    private static String generateRunTimeCommand(String scriptName, String username, Session session){
        String command = "AddRunTime;;" + scriptName + "--" +username + "--" + session.getRunTime();

        return command;
    }

    /**
     * Generate command to add all received / lost / spent items to the database
     * @param scriptName Script Name
     * @param username OSBot username
     * @param session Session
     * @return Command to run
     */
    private static String generateScriptItemCommand(String scriptName, String username, Session session){
        if(session.getItemData().isEmpty()) return "";
        String command = "AddScriptItem;;" + scriptName + "--" + username + "--";
        for(ItemData item : session.getItemData()){
                command += item.getName() + "," + item.getAmount() + "," + item.getStatus() + "!-!";
        }
        return command;
    }

    /**
     * Generate Command to add all items from the current session to the database
     * @param scriptName Script Name
     * @param session Session
     * @return Command to run
     */
    private static String generateAddItemCommand(String scriptName, Session session){
        if(session.getItemData().isEmpty()) return "";
        String command = "AddItem;;" + scriptName +  "--";
        for(ItemData item : session.getItemData()){
            command += item.getName() + "!-!";
        }
        return command;
    }

    /**
     * Combines all exp gains into one command
     * @param session
     * @return
     */
    private static String generateExperienceGainedCommand(String scriptName, String username, Session session){
        if(!session.getExpGained().entrySet().stream().filter(s -> s.getValue() > 0).findAny().isPresent()) return "";
        String command = "AddExperience;;" + scriptName + "--" +username + "--";
        for(Map.Entry<Skill, Integer> expMap : session.getExpGained().entrySet()){
            if(expMap.getValue() > 0){
                command += expMap.getKey().name() + "," + expMap.getValue() + "!-!";
            }
        }
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
