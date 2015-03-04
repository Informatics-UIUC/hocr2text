package edu.illinois.i3.emop.apps.hocr2text;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.Reader;

public class HOCR2Text {
    protected final DocumentBuilderFactory documentBuilderFactory;
    protected XPathExpression xpathOCRPage;
    protected XPathExpression xpathOCRPar;
    protected XPathExpression xpathOCRLine;
    protected XPathExpression xpathOCRXWord;

    public HOCR2Text() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        try {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ignored) {}

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        try {
            xpathOCRPage = xpath.compile("//*[@class='ocr_page']");
            xpathOCRPar = xpath.compile("descendant::*[@class='ocr_par']");
            xpathOCRLine = xpath.compile("descendant::*[@class='ocr_line']");
            xpathOCRXWord = xpath.compile("descendant::*[@class='ocrx_word']");
        } catch (XPathExpressionException ignored) {}
    }

    public String getText(Reader reader) throws Exception {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource(reader);
        Document document = documentBuilder.parse(inputSource);

        Node pageXml = (Node) xpathOCRPage.evaluate(document, XPathConstants.NODE);
        NodeList parsXml = (NodeList) xpathOCRPar.evaluate(pageXml, XPathConstants.NODESET);

        StringBuilder textBuilder = new StringBuilder();

        for (int i = 0, iMax = parsXml.getLength(); i < iMax; i++) {
            Element parXml = (Element) parsXml.item(i);
            NodeList linesXml = (NodeList) xpathOCRLine.evaluate(parXml, XPathConstants.NODESET);

            StringBuilder parTextBuilder = new StringBuilder();
            for (int j = 0, jMax = linesXml.getLength(); j < jMax; j++) {
                Element lineXml = (Element) linesXml.item(j);
                NodeList wordsXml = (NodeList) xpathOCRXWord.evaluate(lineXml, XPathConstants.NODESET);

                StringBuilder lineTextBuilder = new StringBuilder();
                for (int k = 0, kMax = wordsXml.getLength(); k < kMax; k++) {
                    Element wordXml = (Element) wordsXml.item(k);
                    String word = wordXml.getTextContent();

                    lineTextBuilder.append(" ").append(word);
                }

                String lineText = lineTextBuilder.toString();
                if (lineText.length() > 0)
                    lineText = lineText.substring(1);

                parTextBuilder.append("\n").append(lineText);
            }

            String parText = parTextBuilder.toString();
            if (parText.length() > 0)
                parText = parText.substring(1);

            textBuilder.append("\n\n").append(parText);
        }

        String text = textBuilder.toString();
        if (text.length() > 0)
            text = text.substring(2);

        return text;
    }
}
