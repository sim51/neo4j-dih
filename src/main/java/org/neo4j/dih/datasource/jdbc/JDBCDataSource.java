package org.neo4j.dih.datasource.jdbc;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.exception.DIHException;

import java.sql.*;
import java.util.Properties;

/**
 * JDBC datasource.
 */
public class JDBCDataSource extends AbstractDataSource {

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
     * Default constructor.
     *
     * @param config
     */
    public JDBCDataSource(DataSourceType config) {
        super(config);

        this.user = config.getUser();
        this.password = config.getPassword();
        this.url = config.getUrl();
    }

    /**
     *
     * @param entity
     */
    public JDBCResult execute(EntityType entity) throws DIHException {
        return new JDBCResult(user, password, url, entity.getSql());
    }

    /**
     * Getter for user.
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter for user.
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Getter for password.
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for url.
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter for url.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
