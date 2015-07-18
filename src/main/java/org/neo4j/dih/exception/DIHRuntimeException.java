package org.neo4j.dih.exception;

/**
 * DIH runtime exception.
 */
public class DIHRuntimeException extends RuntimeException {

    /**
     * Constructor with an exception.
     *
     * @param e The exception
     */
    public DIHRuntimeException(Exception e) {
        super(e);
    }

    /**
     * Constructor with message and args for its construction.
     *
     * @param message Message with variables of the exception.
     * @param arguments Variables of the message
     */
    public DIHRuntimeException(String message, Object... arguments) {
        super(String.format(message, arguments));
    }
}
