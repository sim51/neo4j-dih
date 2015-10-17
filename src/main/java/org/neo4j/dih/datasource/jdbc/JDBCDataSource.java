package org.neo4j.dih.datasource.jdbc;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Define a JDBC datasource type.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class JDBCDataSource extends AbstractDataSource {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(JDBCDataSource.class);

    /**
     * User to access db.
     */
    private String user;

    /**
     * Password of the user.
     */
    private String password;

    /**
     * JDBC connection url.
     */
    private String url;

    /**
     * The JDBC connection.
     */
    private Connection connection;

    /**
     * Default constructor.
     *
     * @param config a {@link generated.DataSourceType} object.
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public JDBCDataSource(DataSourceType config) throws DIHException {
        super(config);
        this.user = config.getUser();
        this.password = config.getPassword();
        this.url = config.getUrl();
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws DIHException {
        this.connection = getConnection(user, password, url);
    }

    /** {@inheritDoc} */
    @Override
    public JDBCResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new JDBCResultList(connection, TemplateService.getInstance().compile(entity.getSql(), state));
    }

    /** {@inheritDoc} */
    @Override
    public void finish() throws DIHException {
        try {
            if(connection != null)
                this.connection.close();
        } catch (SQLException e) {
            throw new DIHException(e);
        }
    }

    /**
     * Get the connection to the database.
     *
     * @param user a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param url a {@link java.lang.String} object.
     * @return a {@link java.sql.Connection} object.
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    protected Connection getConnection(String user, String password, String url) throws DIHException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        try {
            return DriverManager.getConnection(url, connectionProps);
        } catch (SQLException e) {
            throw new DIHException(e);
        }
    }

}
