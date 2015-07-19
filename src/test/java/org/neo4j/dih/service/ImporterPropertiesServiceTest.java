package org.neo4j.dih.service;

import junit.framework.Assert;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.exception.DIHException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Test class for ImporterPropertiesService.
 */
public class ImporterPropertiesServiceTest extends DIHUnitTest {

    @Test
    public void init_service_that_create_file_should_succeed() throws DIHException {
        ImporterPropertiesService service = new ImporterPropertiesService("target/test-classes/dih", UUID.randomUUID() + ".xml");
    }

    @Test
    public void readProperties_should_succeed() throws DIHException, ParseException {
        ImporterPropertiesService service = new ImporterPropertiesService("target/test-classes/dih", "example_complexe.xml");

        // Assertion
        Assert.assertEquals("2015-07-17 11:50:17", service.getProperty(ImporterPropertiesService.LAST_INDEX_TIME));
        Assert.assertEquals("root", service.getProperty("user"));
        Assert.assertEquals("rootroot", service.getProperty("password"));
    }

    @Test
    public void readPropertiesAsMap_should_succeed() throws DIHException {
        ImporterPropertiesService service = new ImporterPropertiesService("target/test-classes/dih", "example_complexe.xml");
        Map<String, Object> map = service.readPropertiesAsMap();

        // Assertion
        Assert.assertEquals("2015-07-17 11:50:17", map.get(ImporterPropertiesService.LAST_INDEX_TIME));
        Assert.assertEquals("root", map.get("user"));
        Assert.assertEquals("rootroot", map.get("password"));
    }

    @Test
    public void save_should_succeed() throws DIHException {
        String date = SimpleDateFormat.getDateInstance().format(new Date());

        ImporterPropertiesService service = new ImporterPropertiesService("target/test-classes/dih", "example_complexe.xml");
        service.setProperty(ImporterPropertiesService.LAST_INDEX_TIME, date);
        service.save();

        // Assertion
        ImporterPropertiesService service2 = new ImporterPropertiesService("target/test-classes/dih", "example_complexe.xml");
        Assert.assertEquals(date, service2.getProperty(ImporterPropertiesService.LAST_INDEX_TIME));
    }
}
