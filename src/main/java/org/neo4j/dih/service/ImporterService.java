package org.neo4j.dih.service;

import generated.DataConfig;
import generated.DataSourceType;
import generated.EntityType;
import generated.GraphType;
import org.apache.commons.lang.StringUtils;
import org.neo4j.dih.bean.ScriptStatistics;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.datasource.file.csv.CSVDataSource;
import org.neo4j.dih.datasource.file.xml.XMLDataSource;
import org.neo4j.dih.datasource.jdbc.JDBCDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that do the import job.
 */
public class ImporterService {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(ImporterService.class);

    /**
     * The graph database instance.
     */
    private GraphDatabaseService graphDb;

    /**
     * Property service.
     */
    private ImporterPropertiesService properties;

    /**
     * Is in debug mode ?
     * If debug mode is activated, we don't make any write operation.
     */
    private Boolean debug = Boolean.FALSE;

    /**
     * Is in clean mode ?
     * If clean mode is activated, we delete all the database into a separate transaction before importing.
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
     * Current number of iteration.
     * This param is used for <code>periodicCommit</code>
     */
    private Integer iteration = 0;

    /**
     * Current status of periodic commit (for the current <code>graph</code> import).
     * By default, it's null, so there is no periodic commit.
     */
    private Integer periodicCommit;

    /**
     * Some statistic on the script execution.
     */
    public ScriptStatistics stats;

    /**
     * Method that find all configuration files.
     *
     * @return
     */
    public static List<String> getAllConfiguration() {
        Pattern p = Pattern.compile("(.*).xml");
        String path = ClassLoader.getSystemResource("conf/dih/").getFile();
        String[] s = new File(path).list();
        List<String> files = new ArrayList<String>();
        for (String filename : s) {
            Matcher m = p.matcher(filename);
            if (m.matches()) {
                files.add(filename);
            }
        }
        return files;
    }

    /**
     * Constructor.
     */
    public ImporterService(GraphDatabaseService graphDb, String filename, Boolean clean, Boolean debug) throws DIHException {


        // Init services
        this.graphDb = graphDb;
        this.properties = new ImporterPropertiesService(filename);

        // Init config  & datasource
        XmlParserService parser = new XmlParserService();
        this.config = parser.execute(filename);
        this.dataSources = retrieveDataSources();

        if (clean != null)
            this.clean = clean;
        if (debug != null)
            this.debug = debug;

        this.stats = new ScriptStatistics();
    }

    /**
     * Execute the import.
     *
     * @throws DIHException
     */
    public void execute() throws DIHException {

        // saving the starting date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date = sdf.format(new Date());

        // retrieve properties of the job
        Map<String, Object> props = properties.readPropertiesAsMap();
        props.put("debug", this.debug);
        props.put("clean", this.clean);

        // Process the cleaning mod if needed.
        if(!debug) {
            clean();
        }

        // Starting the job import
        starting();

        for (GraphType graph : config.getGraph()) {

            // Init var for the graph import
            script = "";
            iteration = 0;
            periodicCommit = null;

            // If there is a periodic commit
            if (graph.getPeriodicCommit() != null) {
                periodicCommit = graph.getPeriodicCommit().intValue();
            }

            // Let's process the config XML recursively
            Map<String, Object> state = new HashMap<>();
            state.putAll(props);
            List<Object> listEntityOrCypher = graph.getEntityOrCypher();
            process(listEntityOrCypher, state);

            // Execute the cypher script if we are not in debug mode
            if (!debug) {
                cypher(script);
                properties.setProperty(ImporterPropertiesService.LAST_INDEX_TIME, date);
                properties.save();
            }
        }

        // Ending the import
        ending();
    }

    /**
     * Before starting the import, we initializing some stuff.
     *
     * @throws DIHException
     */
    private void starting() throws DIHException {
        for (String key : dataSources.keySet()) {
            dataSources.get(key).start();
        }
    }

    /**
     * Before finishing the import, we closing some stuff.
     *
     * @throws DIHException
     */
    private void ending() throws DIHException {
        for (String key : dataSources.keySet()) {
            dataSources.get(key).finish();
        }
    }

    /**
     * Execute the clean mode if needed.
     */
    protected void clean() {
        // If clean mode is enable
        if (clean) {
            String cleanQuery = "MATCH (n) OPTIONAL MATCH (n)-[r]-(m) DELETE n,r,m;";
            if (!StringUtils.isEmpty(config.getClean())) {
                cleanQuery = config.getClean();
            }
            cypher(cleanQuery);
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
        try (AbstractResultList result = dataSource.execute(entity, state)) {
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
        iteration++;
        script += TemplateService.getInstance().compile(cypher, state);

        // If we are not in debug mode AND there is a periodic commit
        // => if the periodic commit is reached => we make a transaction !
        if (!debug && (periodicCommit != null) && ((iteration % periodicCommit) == 0)) {
            cypher(script);
            script = "";
        }
    }

    /**
     * Retrieve and construct a map of datasource by their name.
     *
     * @return
     */
    protected Map<String, AbstractDataSource> retrieveDataSources() throws DIHException {
        Map<String, AbstractDataSource> dataSources = new HashMap<String, AbstractDataSource>();
        for (DataSourceType dsConfig : config.getDataSource()) {
            // TODO: make this more generic -> type == classname so we can find the class by its name
            // but we need a unique package
            switch (dsConfig.getType()) {
                case "JDBCDataSource":
                    JDBCDataSource jdbcDataSource = new JDBCDataSource(dsConfig);
                    dataSources.put(dsConfig.getName(), jdbcDataSource);
                    break;
                case "CSVDataSource":
                    CSVDataSource csvDataSource = new CSVDataSource(dsConfig);
                    dataSources.put(dsConfig.getName(), csvDataSource);
                    break;
                case "XMLDataSource":
                    XMLDataSource xmlDataSource = new XMLDataSource(dsConfig);
                    dataSources.put(dsConfig.getName(), xmlDataSource);
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
        Result rs = null;

        if (!StringUtils.isEmpty(script)) {
            try (Transaction tx = graphDb.beginTx()) {
                rs = graphDb.execute(script);
                tx.success();

                this.stats.add(rs.getQueryStatistics());
            }
        }
        return rs;
    }

}
