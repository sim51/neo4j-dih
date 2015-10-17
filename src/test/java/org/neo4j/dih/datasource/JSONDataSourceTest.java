package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.file.json.JSONDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.ImporterService;
import org.neo4j.graphdb.Result;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for JSON datasource.
 */
public class JSONDataSourceTest extends DIHUnitTest {

    @Before
    public void prepare() throws SQLException, IOException {
        initGraphDb();
    }

    @Test
    public void execute_on_local_file_should_succeed() throws DIHException {
        JSONDataSource json = getJSONDataSource("file://" + ClassLoader.getSystemResource("datasource/json/example.json").getFile());
        json.start();

        // Assert
        // ~~~~~~
        AbstractResultList result = json.execute(getEntityType("$..book[*]"), new HashMap<String, Object>());

        // First row
        Map<String, Object> row = (Map<String, Object>) result.next();
        Assert.assertEquals("Nigel Rees", row.get("author"));
        Assert.assertEquals("Sayings of the Century", row.get("title"));
        Assert.assertEquals(8.95, row.get("price"));
        Assert.assertNull(row.get("isbn"));

        // Second row
        row = (Map<String, Object>) result.next();
        Assert.assertEquals("Evelyn Waugh", row.get("author"));
        Assert.assertEquals("Sword of Honour", row.get("title"));
        Assert.assertEquals(12.99, row.get("price"));
        Assert.assertNull(row.get("isbn"));

        // Third row
        row = (Map<String, Object>) result.next();
        Assert.assertEquals("Herman Melville", row.get("author"));
        Assert.assertEquals("Moby Dick", row.get("title"));
        Assert.assertEquals(8.99, row.get("price"));
        Assert.assertEquals("0-553-21311-3", row.get("isbn"));

        json.finish();
    }

    @Test
    public void execute_on_local_file_with_definite_path_should_succeed() throws DIHException {
        JSONDataSource json = getJSONDataSource("file://" + ClassLoader.getSystemResource("datasource/json/example.json").getFile());
        json.start();

        // Assert
        // ~~~~~~
        AbstractResultList result = json.execute(getEntityType("$.store.bicycle"), new HashMap<String, Object>());

        // First row
        Map<String, Object> row = (Map<String, Object>) result.next();
        Assert.assertEquals("red", row.get("color"));
        Assert.assertEquals(19.95, row.get("price"));
        Assert.assertFalse((Boolean) ((Map<String, Object>) row.get("equipment")).get("led"));
        Assert.assertTrue((Boolean) ((Map<String, Object>) row.get("equipment")).get("tools"));

        json.finish();
    }

    @Test
    public void import_on_local_json_file_should_succeed() throws DIHException {
        ImporterService importer = new ImporterService(graphDb, "json/example_json.xml", true, false);
        importer.execute();

        // Assert
        // ~~~~~~
        Result rs = cypher("MATCH (b:Book) RETURN b.title AS name ORDER BY name ASC");

        // First row
        Map<String, Object> row = rs.next();
        Assert.assertEquals("Moby Dick", row.get("name"));
        // Second row
        row = rs.next();
        Assert.assertEquals("Sayings of the Century", row.get("name"));
        // Third row
        row = rs.next();
        Assert.assertEquals("Sword of Honour", row.get("name"));
        // 4th row
        row = rs.next();
        Assert.assertEquals("The Lord of the Rings", row.get("name"));
    }

    @After
    public void destroy() throws SQLException {
        destroyGraphDb();
    }

    /**
     * Construct a JSONDataSource.
     *
     * @param url
     * @return
     */
    private JSONDataSource getJSONDataSource(String url) {
        DataSourceType config = new DataSourceType();
        config.setName("json");
        config.setType("JSONDataSource");
        config.setEncoding("UTF-8");
        config.setUrl(url);

        return new JSONDataSource(config);
    }

    /**
     * Construct an EntityType.
     *
     * @param xpath
     * @return
     */
    private EntityType getEntityType(String xpath) {
        EntityType entity = new EntityType();
        entity.setDataSource("json");
        entity.setName("entity");
        entity.setXpath(xpath);
        return  entity;
    }
}
