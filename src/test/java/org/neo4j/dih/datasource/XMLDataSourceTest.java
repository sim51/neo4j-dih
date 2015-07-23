package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import junit.framework.Assert;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.file.csv.CSVDataSource;
import org.neo4j.dih.datasource.file.xml.XMLDataSource;
import org.neo4j.dih.datasource.file.xml.XMLResult;
import org.neo4j.dih.exception.DIHException;

import java.util.HashMap;
import java.util.List;

/**
 * Test class for XML datasource.
 */
public class XMLDataSourceTest extends DIHUnitTest {

    @Test
    public void execute_on_local_file_should_succeed() throws DIHException {
        XMLDataSource xml = getXMLDataSource("file://" + ClassLoader.getSystemResource("datasource/users.xml").getFile());
        xml.start();

        // Assert
        // ~~~~~~
        AbstractResultList result = xml.execute(getEntityType("/users/user"), new HashMap<String, Object>());
        // First row
        XMLResult row = (XMLResult) result.next();
        Assert.assertEquals("1", row.xpath("@id"));
        Assert.assertEquals("root", row.xpath("name"));
        Assert.assertEquals("administrator", row.xpath("role"));
        Assert.assertEquals("localhost", row.xpath("host"));
        Assert.assertEquals("local administrator", row.xpath("description"));
        // Second row
        row = (XMLResult) result.next();
        Assert.assertEquals("2", row.xpath("@id"));
        Assert.assertEquals("root", row.xpath("name"));
        Assert.assertEquals("manager", row.xpath("role"));
        Assert.assertEquals("127.0.0.1", row.xpath("host"));
        Assert.assertEquals("local manager", row.xpath("description"));
        // Third row
        row = (XMLResult) result.next();
        Assert.assertEquals("3", row.xpath("@id"));
        Assert.assertEquals("root", row.xpath("name"));
        Assert.assertEquals("writer", row.xpath("role"));
        Assert.assertEquals("%", row.xpath("host"));
        Assert.assertEquals("distant writer", row.xpath("description"));
        // Testing next
        Assert.assertFalse(result.hasNext());

        xml.finish();
    }

    @Test
    public void execute_on_remote_file_should_succeed() throws DIHException {
        XMLDataSource xml = getXMLDataSource("http://data.nantes.fr/api/publication/24440040400129_VDN_VDN_00041/PRENOM_ENFANT_SLICE_STBL/content/?format=xml");
        xml.start();
        xml.execute(getEntityType("/document/data/element"), new HashMap<String, Object>());
        xml.finish();
    }

    @Test
    public void execute_on_not_existing_file_should_throw_exception() throws DIHException {
        thrown.expect(DIHException.class);
        thrown.expectMessage("java.io.FileNotFoundException: /azertyuiop.xml");

        XMLDataSource xml = getXMLDataSource("file:///azertyuiop.xml");
        xml.start();
        xml.execute(getEntityType(""), new HashMap<String, Object>());
        xml.finish();
    }

    /**
     * Construct a XMLDataSource.
     *
     * @param url
     * @return
     */
    private XMLDataSource getXMLDataSource(String url) {
        DataSourceType config = new DataSourceType();
        config.setName("xml");
        config.setType("XMLDataSource");
        config.setEncoding("UTF-8");
        config.setUrl(url);

        return new XMLDataSource(config);
    }

    /**
     * Construct an EntityType.
     *
     * @param xpath
     * @return
     */
    private EntityType getEntityType(String xpath) {
        EntityType entity = new EntityType();
        entity.setDataSource("xml");
        entity.setName("entity");
        entity.setXpath(xpath);
        return  entity;
    }
}
