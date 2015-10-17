package org.neo4j.dih.datasource.file;

import generated.DataSourceType;
import org.apache.commons.lang.StringUtils;
import org.neo4j.dih.datasource.AbstractDataSource;
import org.neo4j.dih.exception.DIHRuntimeException;

import java.math.BigInteger;

/**
 * Just an abstraction for define file datasource type.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public abstract class AbstractFileDataSource extends AbstractDataSource {

    /**
     * Encoding of the file.
     */
    protected String encoding = "UTF-8";

    /**
     * Url of the file.
     */
    protected String url;

    /**
     * Timeout.
     */
    protected BigInteger timeout = BigInteger.valueOf(1000);

    /**
     * Default constructor.
     *
     * @param config a {@link generated.DataSourceType} object.
     */
    public AbstractFileDataSource(DataSourceType config) {
        super(config);
        this.url = config.getUrl();
        if (StringUtils.isEmpty(config.getUrl())) {
            throw new DIHRuntimeException("File datasource url is mandatory.");
        }
        if (!StringUtils.isEmpty(config.getEncoding())) {
            this.encoding = config.getEncoding();
        }
        if (config.getTimeout() != null) {
            this.timeout = config.getTimeout();
        }
    }

}
