package org.neo4j.dih.datasource.file.csv;

import generated.DataSourceType;
import generated.EntityType;
import org.apache.commons.lang.StringUtils;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

/**
 * Define a CSV datasource type.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class CSVDataSource extends AbstractDataSource {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(CSVDataSource.class);

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
     * If there is a header in CSV file.
     */
    private Boolean withHeaders = Boolean.FALSE;

    /**
     * Timeout.
     */
    private BigInteger timeout = BigInteger.valueOf(1000);

    /**
     * Default constructor.
     *
     * @param config a {@link generated.DataSourceType} object.
     */
    public CSVDataSource(DataSourceType config) {
        super(config);
        this.url = config.getUrl();
        if (StringUtils.isEmpty(config.getUrl())) {
            throw new DIHRuntimeException("CSV datasource url is mandatory.");
        }
        if (!StringUtils.isEmpty(config.getEncoding())) {
            this.encoding = config.getEncoding();
        }
        if (!StringUtils.isEmpty(config.getSeparator())) {
            this.separator = config.getSeparator();
        }
        if (config.getTimeout() != null) {
            this.timeout = config.getTimeout();
        }
        if (config.isWithHeaders() != null && config.isWithHeaders()) {
            this.withHeaders = config.isWithHeaders();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Execute the CSV entity, ie. read the CSV file.
     */
    @Override
    public AbstractResultList execute(EntityType entity, Map<String, Object> state) throws DIHException {
        return new CSVResultList(url, timeout, encoding, separator, withHeaders);
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws DIHException {
    }

    /** {@inheritDoc} */
    @Override
    public void finish() throws DIHException {
    }

}
