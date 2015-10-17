package org.neo4j.dih.datasource.file.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.exception.DIHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * * ResultSet for {@link org.neo4j.dih.datasource.file.json.JSONDataSource} object.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class JSONResultList extends AbstractResultList {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(JSONResultList.class);

    /**
     * ResultSet of the JSONPath query.
     */
    private JSONArray rs;

    /**
     * The current row cursor.
     */
    private Integer current = -1;

    /**
     * Default constructor.
     *
     * @param url       Url of the JSON file
     * @param timeout   Timeout
     * @param encoding  Encoding of the CSV file.
     * @param query     For each JSONPath query
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public JSONResultList(String url, BigInteger timeout, String encoding, String query) throws DIHException {
        try {
            // Get stream of file
            URLConnection connection =new URL(url).openConnection();
            connection.setConnectTimeout(timeout.intValue());

            Configuration conf = Configuration.defaultConfiguration().setOptions(Option.ALWAYS_RETURN_LIST, Option.DEFAULT_PATH_LEAF_TO_NULL);

            this.rs = JsonPath.using(conf).parse(connection.getInputStream(), encoding).read(query);

        } catch (Exception e) {
            throw new DIHException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return current < (rs.size() -1);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> next() {
        current++;
        Map<String, Object> next = (Map<String, Object>) rs.get(current);
        return next;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
    }

}
