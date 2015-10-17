package org.neo4j.dih.service;

import generated.DataConfig;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URISyntaxException;

/**
 * Server that parse the XML DIH config file.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class XmlParserService {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(XmlParserService.class);

    /**
     * The XML unmarshaller.
     */
    private Unmarshaller unmarshaller;


    /**
     * Constructor of the service.
     * If something go wrong, it throw a RuntimeException, but this shouldn't happen.
     */
    public XmlParserService() {
        try {
            // Create the schema
            File schemaFile = new File(ClassLoader.getSystemResource("schema/dataConfig.xsd").getFile());
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(schemaFile);

            // JAXB instance with <code>DataConfig.class</code> context
            JAXBContext jc = JAXBContext.newInstance(DataConfig.class);

            // Create the unmarshaller
            this.unmarshaller = jc.createUnmarshaller();
            this.unmarshaller.setSchema(schema);

        } catch (JAXBException | SAXException e) {
            throw new DIHRuntimeException(e);
        }
    }

    /**
     * Method that parse an XML file.
     *
     * @param filename The DIH XML filename to parse
     * @throws JAXBException if any.
     * @return a {@link generated.DataConfig} object.
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public DataConfig execute(String filename) throws DIHException {
        File file = this.findConfigFileByName(filename);
        try {
            JAXBElement<DataConfig> root = unmarshaller.unmarshal(new StreamSource(file), DataConfig.class);
            return root.getValue();
        } catch (JAXBException e) {
            throw new DIHException(e);
        }
    }

    /**
     * Find the config file by its name.
     * If no file is found, it return an <code>DIHException</code> exception.
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    protected File findConfigFileByName(String name) throws DIHException {
        if (ClassLoader.getSystemResource("conf/dih/" + name) == null) {
            throw new DIHException("Config file %s doesn't exist.", name);
        }
        File file = new File(ClassLoader.getSystemResource("conf/dih/" + name).getFile());
        if (!file.exists())
            throw new DIHException("Config file %s doesn't exist.", name);
        return file;
    }
}
