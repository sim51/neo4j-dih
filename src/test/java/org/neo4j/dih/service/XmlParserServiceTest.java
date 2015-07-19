package org.neo4j.dih.service;

import generated.DataConfig;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.exception.DIHException;

/**
 * Test class for XmlParserService.
 */
public class XmlParserServiceTest extends DIHUnitTest {

    /**
     * Instance of the service to test.
     */
    private XmlParserService service = new XmlParserService("target/test-classes/dih");

    @Test
    public void findConfigFileByName_success() throws DIHException {
        service.findConfigFileByName("example_ok.xml");
    }

    @Test
    public void findConfigFileByName_should_fail_with_a_not_existing_file() throws DIHException {
        thrown.expect(DIHException.class);
        thrown.expectMessage("Config file example_not_found.xml doesn't exist.");
        service.findConfigFileByName("example_not_found.xml");
    }


    /**
     * Testing execute method on normal condition.
     */
    @Test
    public void execute_should_be_ok() throws DIHException {
        DataConfig config = service.execute("example_ok.xml");
        // Testing some equality
        Assert.assertEquals(2, config.getDataSource().size());
        Assert.assertEquals(1, config.getGraph().get(0).getEntityOrCypher().size());
    }

    /**
     * Testing execute method with a malformed xml file.
     */
    @Test
    public void execute_with_malformed_xml_file_should_return_exception() throws DIHException {
        thrown.expect(DIHException.class);
        service.execute("example_malformed.xml");
    }

    /**
     * Testing execute with an invalid xml file.
     */
    @Test
    public void execute_with_invalid_xml_file_should_return_exception() throws DIHException {
        thrown.expect(DIHException.class);
        service.execute("example_invalid.xml");
    }

}
