package org.neo4j.dih.exception;

public class DIHException extends Exception {

    public DIHException(Exception e) {
        super(e);
    }

    public DIHException(String message, Object... arguments) {
        super(String.format(message, arguments));
    }
}
