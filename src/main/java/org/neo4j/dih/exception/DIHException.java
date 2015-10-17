package org.neo4j.dih.exception;

/**
 * DIH exception.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class DIHException extends Exception {

    /**
     * Constructor with an exception.
     *
     * @param e The exception
     */
    public DIHException(Exception e) {
        super(e);
    }

    /**
     * Constructor with message and args for its construction.
     *
     * @param message Message with variables of the exception.
     * @param arguments Variables of the message
     */
    public DIHException(String message, Object... arguments) {
        super(String.format(message, arguments));
    }
}
