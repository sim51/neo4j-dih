package org.neo4j.dih.datasource.file.xml;

import org.neo4j.dih.exception.DIHRuntimeException;
import org.w3c.dom.Node;

import javax.xml.xpath.*;

public class XMLResult {

    private Node node;

    public XMLResult(Node node) {
        this.node = node;
    }

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
