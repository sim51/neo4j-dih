package org.neo4j.dih.exception;

public class DIHRuntimeException extends RuntimeException {

    public DIHRuntimeException(Exception e) {
        super(e);
    }

    public DIHRuntimeException(String message, Exception e) {
        super(message, e);
    }

    public DIHRuntimeException(String message, Object... arguments) {
        super(String.format(message, arguments));
    }
}
