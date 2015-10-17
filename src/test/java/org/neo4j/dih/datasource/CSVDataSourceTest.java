package org.neo4j.dih.datasource;

import generated.DataSourceType;
import generated.EntityType;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;
import org.neo4j.dih.datasource.file.csv.CSVDataSource;
import org.neo4j.dih.exception.DIHException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for CSV datasource.
 */
public class CSVDataSourceTest extends DIHUnitTest {

    @Test
    public void execute_on_local_coma_csv_file_should_succeed() throws DIHException {
        CSVDataSource csv = getCSVDataSource("file://" + ClassLoader.getSystemResource("datasource/file_coma.csv").getFile(), ",", false);
        csv.start();

        // Assert
        // ~~~~~~
        AbstractResultList result = csv.execute(getEntityType(), new HashMap<String, Object>());
        // First row
        Map<String, String> row = (Map<String, String>) result.next();
        Assert.assertEquals("1", row.get(0));
        Assert.assertEquals("administrator", row.get(1));
        // Second row
        row = (Map<String, String>) result.next();
        Assert.assertEquals("2", row.get(0));
        Assert.assertEquals("manager", row.get(1));
        // Third row
        row = (Map<String, String>) result.next();
        Assert.assertEquals("3", row.get(0));
        Assert.assertEquals("writer", row.get(1));
        // Testing next
        Assert.assertFalse(result.hasNext());

        csv.finish();
    }

    @Test
    public void execute_on_local_csv_file_should_succeed() throws DIHException {
        CSVDataSource csv = getCSVDataSource("file://" + ClassLoader.getSystemResource("datasource/file.csv").getFile(), ";", false);
        csv.start();

        // Assert
        // ~~~~~~
        AbstractResultList result = csv.execute(getEntityType(), new HashMap<String, Object>());
        // First row
        Map<String, String> row = (Map<String, String>) result.next();
        Assert.assertEquals("1", row.get(0));
        Assert.assertEquals("administrator", row.get(1));
        // Second row
        row = (Map<String, String>) result.next();
        Assert.assertEquals("2", row.get(0));
        Assert.assertEquals("manager", row.get(1));
        // Third row
        row = (Map<String, String>) result.next();
        Assert.assertEquals("3", row.get(0));
        Assert.assertEquals("writer", row.get(1));
        // Testing next
        Assert.assertFalse(result.hasNext());

        csv.finish();
    }

    @Test
    public void execute_on_remote_file_with_headers_should_succeed() throws DIHException {
        CSVDataSource csv = getCSVDataSource("http://data.nantes.fr/api/publication/24440040400129_VDN_VDN_00041/PRENOM_ENFANT_SLICE_STBL/content/?format=csv" , ",", true);
        csv.start();

        // Assert
        // ~~~~~~
        AbstractResultList result =csv.execute(getEntityType(), new HashMap<String, Object>());

        // First row
        Map<String, String> row = (Map<String, String>) result.next();
        Assert.assertEquals("LOUISE", row.get("PRENOM"));
        Assert.assertEquals("FILLE", row.get("SEXE"));
        Assert.assertEquals("2014", row.get("ANNEE_NAISSANCE"));
        Assert.assertEquals("71.0", row.get("OCCURRENCE"));

        csv.finish();
    }

    @Test
    public void execute_on_not_existing_file_should_throw_exception() throws DIHException {
        thrown.expect(DIHException.class);
        thrown.expectMessage("java.io.FileNotFoundException: /azertyuiop.csv");

        CSVDataSource csv = getCSVDataSource("file:///azertyuiop.csv" , ",", false);
        csv.start();
        csv.execute(getEntityType(), new HashMap<String, Object>());
        csv.finish();
    }

    /**
     * Construct a CSVDataSource.
     *
     * @param url
     * @param separator
     * @return
     */
    private CSVDataSource getCSVDataSource(String url, String separator, Boolean withHeader) {
        DataSourceType config = new DataSourceType();
        config.setName("csv");
        config.setType("CSVDataSource");
        config.setEncoding("UTF-8");
        config.setSeparator(separator);
        config.setUrl(url);
        config.setWithHeader(withHeader);

        return new CSVDataSource(config);
    }

    /**
     * Construct an EntityType.
     * @return
     */
    private EntityType getEntityType() {
        EntityType entity = new EntityType();
        entity.setDataSource("csv");
        entity.setName("line");
        return  entity;
    }
}
