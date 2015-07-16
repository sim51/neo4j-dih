package org.neo4j.dih.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.datasource.csv.CSVDataSource;
import org.neo4j.dih.datasource.jdbc.JDBCDataSource;
import org.neo4j.dih.exception.DIHException;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

/**
 * Test class for ImporterService.
 */
public class ImporterServiceTest extends DIHUnitTest {


    @Before
    public void prepare() throws SQLException {
        initGraphDb();
        initH2();
    }

    @Test
    public void clean_database_should_succeed() throws DIHException {
        ImporterService service = new ImporterService(graphDb, "example_ok.xml", false, false);
        service.cleanDatabase();
    }

    @Test
    public void retrieveDataSources_should_return_csv_and_jdbc() throws DIHException {
        ImporterService service = new ImporterService(graphDb, "example_ok.xml", false, false);
        Map<String, AbstractDataSource> datasources = service.retrieveDataSources();

        // Assert
        // ~~~~~~
        Assert.assertEquals(2, datasources.size());
        // Assert for CSV
        CSVDataSource csv = (CSVDataSource) datasources.get("csv");
        Assert.assertEquals("UTF-8", csv.getEncoding());
        Assert.assertEquals(";", csv.getSeparator());
        Assert.assertEquals("file:///tmp/test.csv", csv.getUrl());
        Assert.assertEquals(BigInteger.valueOf(10000), csv.getTimeout());
        // Assert for JDBC
        JDBCDataSource jdbc= (JDBCDataSource) datasources.get("sql");
        Assert.assertEquals("jdbc:h2:tcp://localhost/~/test", jdbc.getUrl());
        Assert.assertEquals("SA", jdbc.getUser());
        Assert.assertEquals("", jdbc.getPassword());

    }

    @Test
    public void execute_should_succeed() throws DIHException {
        ImporterService importer = new ImporterService(graphDb, "example_only_h2.xml", false, false);
        importer.execute();
    }

    @After
    public void destroy() throws SQLException {
        destroyGraphDb();
        destroyH2();
    }

}
