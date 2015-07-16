package org.neo4j.dih.datasource.csv;

import generated.DataSourceType;
import generated.EntityType;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.datasource.AbstractResult;
import org.neo4j.dih.exception.DIHException;

import java.math.BigInteger;
import java.util.Map;

public class CSVDataSource extends AbstractDataSource {

    /**
     * Encoding of the CSV file.
     */
    private String encoding = "UTF-8";

    /**
     * Url of the CSV file.
     */
    private String url;

    /**
     * CSV Separator
     */
    private String separator = ";";

    /**
     * Timeout.
     */
    private BigInteger timeout = BigInteger.valueOf(1000);

    /**
     * Default constructor.
     *
     * @param config
     */
    public CSVDataSource(DataSourceType config) {
        super(config);

        this.encoding = config.getEncoding();
        this.url = config.getUrl();
        this.separator = config.getSeparator();
        this.timeout = config.getTimeout();
    }

    public AbstractResult execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new CSVResult(url, encoding, separator);
    }

    /**
     * Getter for Encoding.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Setter for encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Getter for Url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter for url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for Separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Setter for separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Getter for Timeout.
     */
    public BigInteger getTimeout() {
        return timeout;
    }

    /**
     * Setter for timeout.
     */
    public void setTimeout(BigInteger timeout) {
        this.timeout = timeout;
    }
}
