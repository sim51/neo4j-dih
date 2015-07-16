package org.neo4j.dih.datasource;


import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class that represent a Result of a datasource execution.
 */
public abstract class AbstractResult implements Iterator<Map<String, Object>>, Closeable {

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
