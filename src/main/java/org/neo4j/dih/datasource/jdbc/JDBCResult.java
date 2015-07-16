package org.neo4j.dih.datasource.jdbc;

import org.neo4j.dih.datasource.AbstractResult;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.exception.DIHRuntimeException;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Result of a JDBCDataSource execution.
 */
public class JDBCResult extends AbstractResult {

    /**
     * The JDBC connection.
     */
    private Connection connection;

    /**
     * The JDBC statement.
     */
    private Statement statement;

    /**
     * ResultSet of the query.
     */
    private ResultSet result;

    /**
     * The current row.
     */
    private Map<String, Object> current;

    /**
     * Constructor.
     *
     * @param user
     * @param password
     * @param url
     * @param query
     * @throws DIHException
     */
    public JDBCResult(String user, String password, String url, String query) throws DIHException {
        try {
            this.connection = getConnection(user, password, url);
            this.statement = connection.createStatement();
            this.result = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new DIHException("Error when trying to connect & execute query %s on database %s : %s", query, url, e.getMessage());
        }
        step();
    }

    /**
     * Get the connection to the database.
     *
     * @return
     * @throws SQLException
     */
    protected Connection getConnection(String user, String password, String url) throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        return DriverManager.getConnection(url, connectionProps);
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public Map<String, Object> next() {
        Map<String, Object> rs = current;
        step();
        return rs;
    }

    @Override
    public void close() throws IOException {
        try {
            this.statement.close();
            this.connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Doing a forward step into the result list.
     */
    private void step() {
        Map<String, Object> rs = null;
        try {
            if(result.next()) {
                rs = new HashMap<>();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    String columnName = result.getMetaData().getColumnName(i);
                    Object value = result.getObject(columnName);
                    rs.put(columnName, value);
                }
            }
        } catch (SQLException e) {
           throw new DIHRuntimeException(e);
        }
        current = rs;
    }

}
