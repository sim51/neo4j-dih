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
 * JDBC datasource.
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
     * @param config
     */
    public JDBCDataSource(DataSourceType config) throws DIHException {
        super(config);
        this.user = config.getUser();
        this.password = config.getPassword();
        this.url = config.getUrl();
    }

    @Override
    public void start() throws DIHException {
        this.connection = getConnection(user, password, url);
    }

    @Override
    public JDBCResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new JDBCResultList(connection, TemplateService.compile(entity.getSql(), state));
    }

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
     * @return
     * @throws SQLException
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
