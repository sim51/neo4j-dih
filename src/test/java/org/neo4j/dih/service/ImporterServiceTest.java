package org.neo4j.dih.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.graphdb.Result;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Test class for ImporterService.
 */
public class ImporterServiceTest extends DIHUnitTest {

    @Before
    public void prepare() throws SQLException, IOException {
        initGraphDb();
        initH2();
    }

    @Test
    public void retrieveDataSources_should_return_csv_and_jdbc() throws DIHException {
        ImporterService service = new ImporterService(graphDb, "example_ok.xml", false, false);
        Map<String, AbstractDataSource> datasources = service.retrieveDataSources();

        // Assert
        // ~~~~~~
        Assert.assertEquals(2, datasources.size());
        Assert.assertNotNull(datasources.get("csv"));
        Assert.assertNotNull(datasources.get("sql"));

    }

    @Test
    public void execute_only_h2_should_succeed() throws DIHException {
        ImporterService importer = new ImporterService(graphDb, "example_only_h2_with_periodic_commit.xml", false, false);
        importer.execute();

        // Assert
        // ~~~~~~
        Result rs = importer.cypher("MATCH (u:User)-[:OF_HOST]->(h:Host) RETURN u.name AS user, h.name AS host ORDER BY host ASC");
        // First row
        Map<String, Object> firstRow = rs.next();
        Assert.assertEquals("root", firstRow.get("user"));
        Assert.assertEquals("%", firstRow.get("host"));
        // Second row
        Map<String, Object> secRow = rs.next();
        Assert.assertEquals("root", secRow.get("user"));
        Assert.assertEquals("127.0.0.1", secRow.get("host"));
        // Third row
        Map<String, Object> thRow = rs.next();
        Assert.assertEquals("root", thRow.get("user"));
        Assert.assertEquals("localhost", thRow.get("host"));
    }

    @Test
    public void execute_complexe_should_succeed() throws DIHException {

        ImporterService importer = new ImporterService(graphDb, "example_complexe.xml", false, false);
        importer.execute();

        // Assert
        // ~~~~~~
        Result rs = importer.cypher("MATCH (u:User)-[:OF_HOST]->(h:Host), (u)-[r:HAS_ROLE]->(role:Role) RETURN u.id AS id, u.name AS user, h.name AS host, role.name AS role, r.description AS description ORDER BY id ASC");
        // First row
        Map<String, Object> row = rs.next();
        Assert.assertEquals("1", row.get("id"));
        Assert.assertEquals("root", row.get("user"));
        Assert.assertEquals("localhost", row.get("host"));
        Assert.assertEquals("administrator", row.get("role"));
        Assert.assertEquals("local administrator", row.get("description"));
        // Second row
        row = rs.next();
        Assert.assertEquals("2", row.get("id"));
        Assert.assertEquals("root", row.get("user"));
        Assert.assertEquals("127.0.0.1", row.get("host"));
        Assert.assertEquals("manager", row.get("role"));
        Assert.assertEquals("local manager", row.get("description"));
        // Third row
        row = rs.next();
        Assert.assertEquals("3", row.get("id"));
        Assert.assertEquals("%", row.get("host"));
        Assert.assertEquals("root", row.get("user"));
        Assert.assertEquals("writer", row.get("role"));
        Assert.assertEquals("distant writer", row.get("description"));
    }

    @Test
    public void getAllConfiguration_should_succeed() throws DIHException {
        List<String> files = ImporterService.getAllConfiguration();

        // Assert
        // ~~~~~~
        Assert.assertEquals(true, files.contains("example_complexe.xml"));
        Assert.assertEquals(true, files.contains("example_invalid.xml"));
        Assert.assertEquals(true, files.contains("example_malformed.xml"));
        Assert.assertEquals(true, files.contains("example_ok.xml"));
        Assert.assertEquals(true, files.contains("example_only_h2_with_periodic_commit.xml"));
        Assert.assertEquals(true, files.contains("csv/example_csv.xml"));
    }

    @After
    public void destroy() throws SQLException {
        destroyGraphDb();
        destroyH2();
    }

}
