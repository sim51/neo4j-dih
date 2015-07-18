package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.exception.DIHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Abstract class that represent a datasource.
 */
public abstract class AbstractDataSource {

    /**
     * All datasource must have a constructor with a <code>DataSourceType</code>
     *
     * @param config Xml bean object that represent a datasource.
     */
    public AbstractDataSource(DataSourceType config){};

    /**
     * All datasource must have an <code>execute</code> method to perform the entity work.
     *
     * @param entity Xml bean Entity.
     * @param state The current state of variables of the import.
     * @return
     * @throws DIHException
     */
    public abstract AbstractResult execute(EntityType entity, Map<String, Object> state) throws DIHException;
}
