package me.mykindos.client.tracker.exceptions;

/**
 * Thrown when the setup process is followed incorrectly.
 */
public class InvalidSetupException extends Exception {

    /**
     * Create the exception
     * @param s
     */
    public InvalidSetupException(String s){
        super(s);
    }
}
