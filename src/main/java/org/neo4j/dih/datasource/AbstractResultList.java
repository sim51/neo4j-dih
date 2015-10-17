package org.neo4j.dih.datasource;


import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class that represent a Result of a datasource execution.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public abstract class AbstractResultList implements Iterator<Object>, Closeable {

    /** {@inheritDoc} */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
