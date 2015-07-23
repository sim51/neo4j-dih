package org.neo4j.dih.datasource.file.xml;

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

/**
 * Result for a XMLDataSource.
 */
public class XMLResultList extends AbstractResultList {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(XMLResultList.class);

    /**
     * Stream of the file.
     */
    private InputStream stream;

    /**
     * List of entity return by xpath expression.
     */
    private NodeList nodeList;

    /**
     * The current row cursor.
     */
    private Integer current = -1;

    /**
     * Constructor.
     *
     * @param url       Url of the CSV file
     * @param timeout   Tileout
     * @param encoding  Encoding of the CSV file.
     * @param query     For each XPATH query
     * @throws DIHException
     */
    public XMLResultList(String url, BigInteger timeout, String encoding, String query) throws DIHException {
        try {
            // Get stream of file
            URLConnection connection =new URL(url).openConnection();
            connection.setConnectTimeout(timeout.intValue());
            this.stream = connection.getInputStream();

            // Build the Xpath parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(query);
            this.nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new DIHException(e);
        }

    }

    @Override
    public boolean hasNext() {
        return current < (nodeList.getLength() -1);
    }

    @Override
    public XMLResult next() {
        current++;
        Node node = nodeList.item(current);
        return new XMLResult(node);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

}
