package org.neo4j.dih.service;

import org.junit.Assert;
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
        ImporterPropertiesService service = new ImporterPropertiesService( UUID.randomUUID() + ".xml");
    }

    @Test
    public void readProperties_should_succeed() throws DIHException, ParseException {
        ImporterPropertiesService service = new ImporterPropertiesService( "example_complexe.xml");

        // Assertion
        Assert.assertEquals("2015-07-17 11:50:17", service.getProperty(ImporterPropertiesService.LAST_INDEX_TIME));
        Assert.assertEquals("root", service.getProperty("user"));
        Assert.assertEquals("rootroot", service.getProperty("password"));
    }

    @Test
    public void readPropertiesAsMap_should_succeed() throws DIHException {
        ImporterPropertiesService service = new ImporterPropertiesService("example_complexe.xml");
        Map<String, Object> map = service.readPropertiesAsMap();

        // Assertion
        Assert.assertEquals("2015-07-17 11:50:17", map.get(ImporterPropertiesService.LAST_INDEX_TIME));
        Assert.assertEquals("root", map.get("user"));
        Assert.assertEquals("rootroot", map.get("password"));
    }

    @Test
    public void save_should_succeed() throws DIHException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date = sdf.format(new Date());
        String name = UUID.randomUUID() + ".xml";

        ImporterPropertiesService service = new ImporterPropertiesService(name);
        service.setProperty(ImporterPropertiesService.LAST_INDEX_TIME, date);
        service.save();

        // Assertion
        ImporterPropertiesService service2 = new ImporterPropertiesService(name);
        Assert.assertEquals(date, service2.getProperty(ImporterPropertiesService.LAST_INDEX_TIME));
    }
}
