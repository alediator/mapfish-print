package org.mapfish.print;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

public class CustomXPathTest extends PrintTestCase {
    public CustomXPathTest(String name) {
        super(name);
    }

    public void testFactorArray() {
        CustomXPath functs = new CustomXPath();
        assertEquals("2,4,6", functs.factorArray("1,2,3", 2));
        assertEquals("2,4,6", functs.factorArray("1, 2, 3", 2));
    }

    public void testXslt() throws TransformerException, IOException {
        final StringReader xsltStream = new StringReader(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" +
                        "                xmlns:xalan=\"http://xml.apache.org/xalan\"\n" +
                        "                xmlns:custom=\"Custom\"\n" +
                        "                version=\"1.0\">\n" +
                        "  <xalan:component prefix=\"custom\" functions=\"factorArray\">\n" +
                        "    <xalan:script lang=\"javaclass\" src=\"org.mapfish.print.CustomXPath\"/>\n" +
                        "  </xalan:component>\n" +
                        "  <xsl:template match=\"/*\">\n" +
                        "    <tutu b=\"{custom:factorArray(@a,3)}\"/>\n" +
                        "  </xsl:template>\n" +
                        "</xsl:stylesheet>");
        final StringReader xmlStream = new StringReader(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<toto a=\"1,2,3\"/>");


        DOMResult transformedSvg = new DOMResult();
        final TransformerFactory factory = TransformerFactory.newInstance();
        javax.xml.transform.Transformer xslt = factory.newTransformer(new StreamSource(xsltStream));
        xslt.transform(new StreamSource(xmlStream), transformedSvg);
        Document doc = (Document) transformedSvg.getNode();

        Node main = doc.getFirstChild();
        assertEquals("tutu", main.getNodeName());
        final Node attrB = main.getAttributes().getNamedItem("b");
        assertNotNull(attrB);
        assertEquals("3,6,9", attrB.getNodeValue());

        xmlStream.close();
        xsltStream.close();
    }
}
