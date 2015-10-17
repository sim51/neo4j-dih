package org.neo4j.dih.datasource.file.xml;

import org.neo4j.dih.exception.DIHRuntimeException;
import org.w3c.dom.Node;

import javax.xml.xpath.*;

/**
 * Row representation of {@link org.neo4j.dih.datasource.file.xml.XMLResultList} object.
 * It's just a Node wrapper.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class XMLResult {

    /**
     * The XML node
     */
    private Node node;

    /**
     * Constructor for XMLResult.
     *
     * @param node a {@link org.w3c.dom.Node} object.
     */
    public XMLResult(Node node) {
        this.node = node;
    }

    /**
     * Permit de make a xpath query on the node.
     *
     * @param query a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String xpath(String query) {
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(query);
            return (String) expr.evaluate(node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new DIHRuntimeException(e);
        }
    }
}
