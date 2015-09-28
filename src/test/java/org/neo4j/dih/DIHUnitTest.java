package org.neo4j.dih;

import org.h2.tools.Server;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DIHUnitTest {

    /**
     * Graph database instance.
     */
    protected GraphDatabaseService graphDb;

    /**
     * Graph database server.
     */
    protected ServerControls server;

    /**
     * H2 server
     */
    protected Server h2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Create an in memory graph database.
     */
    protected void initGraphDb() {
        // Create Neo4j in memory database.
        this.graphDb = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder("target/test-classes")
                .loadPropertiesFromFile("target/test-classes/neo4j.properties")
                .newGraphDatabase();
    }

    /**
     * Destroy the graph database.
     */
    protected void destroyGraphDb() {
        this.graphDb.shutdown();
    }

    /**
     * Create an in memory graph database server.
     */
    protected void initServerWithExtension() throws IOException {
        this.server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/dih", DataImportHandlerExtension.class)
                .newServer();
    }

    /**
     * Destroy the in memory graph database server.
     */
    protected void destroyServerWithExtension() {
        this.server.close();
    }

    /**
     * Create an in memrory H2 database.
     */
    protected void initH2() throws SQLException {
        // Create H2 database
        this.h2 = Server.createTcpServer().start();
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test;INIT=runscript from 'classpath:/datasource/init.sql'", "SA", "");
        connection.close();
    }

    /**
     * Destroy the H2 database.
     */
    protected void destroyH2() throws SQLException {
        this.h2.stop();
    }
}
