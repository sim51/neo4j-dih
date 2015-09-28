package org.neo4j.dih.datasource.file.xml;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.datasource.file.AbstractFileDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.TemplateService;

import java.util.Map;

public class XMLDataSource extends AbstractFileDataSource {

    /**
     * Default constructor.
     *
     * @param config
     */
    public XMLDataSource(DataSourceType config) {
        super(config);
    }

    /**
     * Execute the XML entity, ie. read the CSV file.
     *
     * @param entity The entity to execute.
     * @param state  Current state of all declare variable.
     * @return
     * @throws DIHException
     */
    @Override
    public AbstractResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new XMLResultList(url, timeout, encoding, TemplateService.getInstance().compile(entity.getXpath(), state));
    }

    @Override
    public void start() throws DIHException {
    }

    @Override
    public void finish() throws DIHException {
    }

}
