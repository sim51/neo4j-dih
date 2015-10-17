package org.neo4j.dih.datasource.file.json;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.datasource.file.AbstractFileDataSource;
import org.neo4j.dih.datasource.file.json.JSONResultList;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.TemplateService;

import java.util.Map;

public class JSONDataSource extends AbstractFileDataSource {

    /**
     * Default constructor.
     *
     * @param config
     */
    public JSONDataSource(DataSourceType config) {
        super(config);
    }

    /**
     * Execute the JSON entity, ie. read the JSON file.
     *
     * @param entity The entity to execute.
     * @param state  Current state of all declare variable.
     * @return
     * @throws DIHException
     */
    @Override
    public AbstractResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new JSONResultList(url, timeout, encoding, TemplateService.getInstance().compile(entity.getXpath(), state));
    }

    @Override
    public void start() throws DIHException {
    }

    @Override
    public void finish() throws DIHException {
    }

}
