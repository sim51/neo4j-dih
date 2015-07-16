package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.exception.DIHException;

public abstract class AbstractDataSource {

    public AbstractDataSource(DataSourceType config){};

    public abstract AbstractResult execute(EntityType entity) throws DIHException;
}
