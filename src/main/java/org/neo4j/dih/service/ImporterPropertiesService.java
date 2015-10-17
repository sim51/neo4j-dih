package org.neo4j.dih.service;

import org.neo4j.dih.exception.DIHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Service that generate a property file for each importer.
 *
 * @author bsimard
 * @version $Id: $Id
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
     *  Default value for  <code>LAST_INDEX_TIME</code>.
     *  */
    public static final String DEFAULT_VALUE_LAST_INDEX_TIME = "1970-01-01 00:00:00";

    /**
     * The property file.
     */
    private File file;

    /**
     * List of all properties
     */
    private Properties properties;

    /**
     * By calling the constructor, if property file doesn't exist, it will be created.
     *
     * @param name Name of the XML file for the import.
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public ImporterPropertiesService(String name) throws DIHException {
        String fileFolder = ClassLoader.getSystemResource("conf/dih/").getFile();
        File file = new File(fileFolder, name.replace(".xml", ".properties"));
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
     * @throws org.neo4j.dih.exception.DIHException if any.
     * @return a {@link java.util.Properties} object.
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
     * @throws org.neo4j.dih.exception.DIHException if any.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Object> readPropertiesAsMap() throws DIHException {
        Map<String, Object> props = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            props.put(key, properties.getProperty(key));
        }
        // adding last_index_time if it doesn't exist
        if(!props.containsKey(ImporterPropertiesService.LAST_INDEX_TIME)) {
            props.put(ImporterPropertiesService.LAST_INDEX_TIME, DEFAULT_VALUE_LAST_INDEX_TIME);
        }
        return props;
    }

    /**
     * Retrieve the value of a property.
     * If property is not found, we return <code>null</code>.
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
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
     *
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public void save() throws DIHException {
        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, null);
        } catch (Exception e) {
            throw new DIHException(e);
        }
    }

}

