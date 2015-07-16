package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.exception.DIHException;

import java.util.Map;

public abstract class AbstractDataSource {

    public AbstractDataSource(DataSourceType config){};

    public abstract AbstractResult execute(EntityType entity, Map<String, Object> state) throws DIHException;
}
