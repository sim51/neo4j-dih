package org.neo4j.dih.service;

import org.neo4j.dih.exception.DIHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Service that generate a property file for each importer.
 */
public class ImporterPropertiesService {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    /**
     * Name of the property for the last index date.
     */
    public static final String LAST_INDEX_TIME = "last_index_time";

    /**
     * The property file.
     */
    private File file;

    /**
     * List of all properties
     */
    private Properties properties;

    /**
     * Constructor.
     *
     * @param configPath Neo4j path where all DIH config are.
     * @param name       Name of the XML file for the import.
     * @throws DIHException
     */
    public ImporterPropertiesService(String configPath, String name) throws DIHException {
        File file = new File(configPath, name.replace(".xml", ".properties"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new DIHException("Cant' create or read import property file %s : %s", name, e.getMessage());
            }
        }
        this.file = file;
        this.properties = readProperties();
    }

    /**
     * Retrieve all properties of the file.
     *
     * @return
     * @throws DIHException
     */
    public Properties readProperties() throws DIHException {
        try (FileInputStream fis = new FileInputStream(file.getPath())) {
            Properties props = new Properties();
            props.load(fis);
            return props;
        } catch (Exception e) {
            throw new DIHException(e);
        }
    }

    /**
     * Retrieve all properties of the file, and generate a <code>Map</code>.
     *
     * @return
     * @throws DIHException
     */
    public Map<String, Object> readPropertiesAsMap() throws DIHException {
        Map<String, Object> props = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            props.put(key, properties.getProperty(key));
        }
        return props;
    }

    /**
     * Retrieve the value of a property.
     * If property is not found, we return <code>null</code>.
     *
     * @param name
     * @return
     */
    public Object getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Update or create a property.
     *
     * @param name  Name of the property to create/update.
     * @param value Value of the property to create/update.
     */
    public void setProperty(String name, Object value) {
        properties.setProperty(name, (String) value);
    }

    /**
     * Save the property file.
     */
    public void save() throws DIHException {
        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, null);
        } catch (Exception e) {
            throw new DIHException(e);
        }
    }

}

