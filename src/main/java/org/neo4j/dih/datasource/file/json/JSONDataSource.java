package org.neo4j.dih.datasource.file.json;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.datasource.file.AbstractFileDataSource;
import org.neo4j.dih.datasource.file.json.JSONResultList;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.TemplateService;

import java.util.Map;

/**
 * Define a JSON datasource type.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class JSONDataSource extends AbstractFileDataSource {

    /**
     * Default constructor.
     *
     * @param config a {@link generated.DataSourceType} object.
     */
    public JSONDataSource(DataSourceType config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     *
     * Execute the JSON entity, ie. read the JSON file.
     */
    @Override
    public AbstractResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new JSONResultList(url, timeout, encoding, TemplateService.getInstance().compile(entity.getXpath(), state));
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws DIHException {
    }

    /** {@inheritDoc} */
    @Override
    public void finish() throws DIHException {
    }

}
