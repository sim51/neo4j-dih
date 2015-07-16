package org.neo4j.dih.service;

import generated.DataConfig;
import generated.DataSourceType;
import generated.EntityType;
import generated.GraphType;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.datasource.AbstractResult;
import org.neo4j.dih.datasource.csv.CSVDataSource;
import org.neo4j.dih.datasource.jdbc.JDBCDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.jmx.JmxUtils;

import javax.management.ObjectName;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that do the import job.
 */
public class ImporterService {

    /**
     * The graph database instance.
     */
    private GraphDatabaseService graphDb;

    /**
     * Is in debug mode ?
     */
    private Boolean debug = Boolean.FALSE;

    /**
     * Is in clean mode ?
     */
    private Boolean clean = Boolean.FALSE;

    /**
     * The XML config.
     */
    private DataConfig config;

    /**
     * Map of available datasource.
     */
    private Map<String, AbstractDataSource> dataSources;

    /**
     * The cypher generated script.
     */
    private String script = "";

    /**
     * Constructor.
     */
    public ImporterService(GraphDatabaseService graphDb, String filename, Boolean clean, Boolean debug) throws DIHException {
        // Init services
        this.graphDb = graphDb;

        // Init config  & datasource
        ObjectName jmxConfigurationObject = JmxUtils.getObjectName(graphDb, "Configuration");
        String confDir = JmxUtils.getAttribute(jmxConfigurationObject, "store_dir");
        XmlParserService parser = new XmlParserService(confDir + "/dih");
        this.config = parser.execute(filename);
        this.dataSources = retrieveDataSources();
    }

    /**
     * Execute the import
     *
     * @throws JAXBException
     */
    public void execute() throws DIHException {

        // Init var that contains the cypher generated script
        script = "";

        // If clean mode is enable
        if (clean) {
            cypher("MATCH (n) OPTIONAL MATCH (n)-[r]-(m) DELETE n,r,m;");
        }

        // If there is a periodic commit
        for (GraphType graph : config.getGraph()) {
            if (graph.getPeriodicCommit() != null) {
                script = "USING PERIODIC COMMIT " + graph.getPeriodicCommit();
            }

            // Let's process the config XML recursively
            Map<String, Object> state = new HashMap<>();
            List<Object> listEntityOrCypher = graph.getEntityOrCypher();
            process(listEntityOrCypher, state);

            // Execute the cypher script
            cypher(script);
        }
    }

    /**
     * Process recursively a list of entity or cypher.
     *
     * @param listEntityOrCypher
     * @param state
     * @throws DIHException
     */
    protected void process(List<Object> listEntityOrCypher, Map<String, Object> state) throws DIHException {
        for (Object obj : listEntityOrCypher) {
            if (obj instanceof EntityType) {
                processEntity((EntityType) obj, state);
            } else {
                processCypher((String) obj, state);
            }
        }
    }

    /**
     * Process an Entity.
     *
     * @param entity
     * @throws DIHException
     */
    protected void processEntity(EntityType entity, Map<String, Object> state) throws DIHException {
        AbstractDataSource dataSource = dataSources.get(entity.getDataSource());
        try (AbstractResult result = dataSource.execute(entity, state)) {
            while (result.hasNext()) {
                Map<String, Object> childState = state;
                childState.put(entity.getName(), result.next());
                process(entity.getEntityOrCypher(), childState);
            }
        } catch (IOException e) {
            throw new DIHException(e);
        }
    }

    /**
     * Process a Cypher query.
     *
     * @param cypher
     */
    protected void processCypher(String cypher, Map<String, Object> state) {
        script += TemplateService.compile(cypher, state);
    }

    /**
     * Retrieve and construct a map of datasource by their name.
     *
     * @return
     */
    protected Map<String, AbstractDataSource> retrieveDataSources() {
        Map<String, AbstractDataSource> dataSources = new HashMap<String, AbstractDataSource>();
        for (DataSourceType dsConfig : config.getDataSource()) {
            // TODO: make this more generic -> type == classname so we can find the class by its name
            switch (dsConfig.getType()) {
                case "JDBCDataSource":
                    JDBCDataSource jdbcDataSource = new JDBCDataSource(dsConfig);
                    dataSources.put(dsConfig.getName(), jdbcDataSource);
                    break;
                case "CSVDataSource":
                    CSVDataSource csvDataSource = new CSVDataSource(dsConfig);
                    dataSources.put(dsConfig.getName(), csvDataSource);
                    break;
                default:
                    throw new IllegalArgumentException("Type on datasource is mandatory");
            }
        }
        return dataSources;
    }

    /**
     * Execute a Cypher query.
     *
     * @param script
     * @return
     */
    protected Result cypher(String script) {
        Result rs;
        try (Transaction tx = graphDb.beginTx()) {
            rs = graphDb.execute(script);
            tx.success();
        }
        return rs;
    }

}
