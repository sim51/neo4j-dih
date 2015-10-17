package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.exception.DIHException;

import java.util.Map;

/**
 * Abstract class that represent a datasource.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public abstract class AbstractDataSource {

    /**
     * Datasource must have a constructor with a <code>DataSourceType</code>
     *
     * @param config Xml bean object that represent a datasource.
     */
    public AbstractDataSource(DataSourceType config){};

    /**
     * This method is called when a job is starting. It permits to make some initialising stuff before starting a import job.
     * For example, on <code>JDBCDataSource</code> we create a database connection.
     *
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public abstract void start() throws DIHException;

    /**
     * Datasource must have an <code>execute</code> method to perform the entity work.
     *
     * @param entity Xml bean Entity.
     * @param state The current state of variables of the import.
     * @throws org.neo4j.dih.exception.DIHException if any.
     * @return a {@link org.neo4j.dih.datasource.AbstractResultList} object.
     */
    public abstract AbstractResultList execute(EntityType entity, Map<String, Object> state) throws DIHException;

    /**
     * This method is called when a job is ending. It permits to close all open resources.
     * For example, on <code>JDBCDataSource</code> we release the database connection.
     *
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public abstract void finish() throws DIHException;
}
