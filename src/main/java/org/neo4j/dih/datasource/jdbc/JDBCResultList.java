package org.neo4j.dih.datasource.jdbc;

import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * * ResultSet for {@link org.neo4j.dih.datasource.jdbc.JDBCDataSource} object.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class JDBCResultList extends AbstractResultList {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(JDBCResultList.class);

    /**
     * The JDBC statement.
     */
    private Statement statement;

    /**
     * ResultSet of the query.
     */
    private ResultSet result;

    /**
     * The current row cursor.
     */
    private Map<String, Object> current;

    /**
     * Default constructor.
     *
     * @param query a {@link java.lang.String} object.
     * @throws org.neo4j.dih.exception.DIHException if any.
     * @param connection a {@link java.sql.Connection} object.
     */
    public JDBCResultList(Connection connection, String query) throws DIHException {
        try {
            this.statement = connection.createStatement();
            this.result = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new DIHException("Error when trying to connect & execute query %s : %s", query, e.getMessage());
        }
        step();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return current != null;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> next() {
        Map<String, Object> rs = current;
        step();
        return rs;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        try {
            this.statement.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Doing a step forward into the result list.
     */
    private void step() {
        Map<String, Object> rs = null;
        try {
            if (result.next()) {
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
