package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.jdbc.JDBCDataSource;
import org.neo4j.dih.exception.DIHException;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Test class for JDBC datasource.
 */
public class JDBCDataSourceTest extends DIHUnitTest {

    @Before
    public void prepare() throws SQLException {
        initGraphDb();
        initH2();
    }

    @Test
    public void execute_on_valid_database_should_succeed() throws DIHException {
        JDBCDataSource jdbc = getJDBCDataSource("jdbc:h2:tcp://localhost/~/test", "SA", "");

        // Assert
        // ~~~~~~
        AbstractResult result = jdbc.execute(getEntityType("SELECT * FROM PEOPLE"), new HashMap<String, Object>());
        // First row
        HashMap<String, Object> row = (HashMap<String, Object>) result.next();
        Assert.assertEquals("1", row.get("ID").toString());
        Assert.assertEquals("root", row.get("USER"));
        Assert.assertEquals("localhost", row.get("HOST"));
        // Second row
        row = (HashMap<String, Object>) result.next();
        Assert.assertEquals("2", row.get("ID").toString());
        Assert.assertEquals("root", row.get("USER"));
        Assert.assertEquals("127.0.0.1", row.get("HOST"));
        // Third row
        row = (HashMap<String, Object>) result.next();
        Assert.assertEquals("3", row.get("ID").toString());
        Assert.assertEquals("root", row.get("USER"));
        Assert.assertEquals("%", row.get("HOST"));
        // Testing next
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void execute_on_invalid_database_should_fail() throws DIHException {
        thrown.expect(DIHException.class);
        thrown.expectMessage("Error when trying to connect & execute query SELECT * FROM PEOPLE on database jdbc:h2:tcp://1.1.1.1/~/test");
        thrown.expectMessage("java.net.SocketTimeoutException");

        JDBCDataSource jdbc = getJDBCDataSource("jdbc:h2:tcp://1.1.1.1/~/test", "SA", "");
        jdbc.execute(getEntityType("SELECT * FROM PEOPLE"), new HashMap<String, Object>());
    }

    @Test
    public void execute_on_valid_database_with_bad_sql_should_fail() throws DIHException {
        thrown.expect(DIHException.class);
        thrown.expectMessage("Error when trying to connect & execute query SELECT * FROMPEOPLE on database jdbc:h2:tcp://localhost/~/test");
        thrown.expectMessage("Syntax error in SQL statement");

        JDBCDataSource jdbc = getJDBCDataSource("jdbc:h2:tcp://localhost/~/test", "SA", "");
        jdbc.execute(getEntityType("SELECT * FROMPEOPLE"), new HashMap<String, Object>());
    }

    /**
     * Construct a JDBCDataSource.
     *
     * @param url
     * @return
     */
    private JDBCDataSource getJDBCDataSource(String url, String user, String password) {
        DataSourceType config = new DataSourceType();
        config.setName("jdbc");
        config.setType("JDBCDataSource");
        config.setUrl(url);
        config.setUser(user);
        config.setPassword(password);

        return new JDBCDataSource(config);
    }

    /**
     * Construct an EntityType.
     * @return
     */
    private EntityType getEntityType(String sql) {
        EntityType entity = new EntityType();
        entity.setDataSource("jdbc");
        entity.setName("people");
        entity.setSql(sql);
        return  entity;
    }

    @After
    public void destroy() throws SQLException {
        destroyGraphDb();
        destroyH2();
    }
}
