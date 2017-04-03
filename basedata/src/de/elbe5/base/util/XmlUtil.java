/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.util;

import org.w3c.dom.*;
import sun.misc.BASE64Encoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class XmlUtil {

    public static SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public static Document createXmlDocument() {
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.setXmlVersion("1.0");
            return doc;
        } catch (Exception e) {
            return null;
        }
    }

    public static Element createRootNode(Document doc, String rootName) {
        Element rootNode = doc.createElement(rootName);
        doc.appendChild(rootNode);
        return rootNode;
    }

    public static Element addNode(Document doc, Element parentNode, String name) {
        Element childNode = doc.createElement(name);
        parentNode.appendChild(childNode);
        return childNode;
    }

    public static void addTextNode(Document doc, Element parentNode, String name, String text) {
        Element childNode= addNode(doc, parentNode, name);
        childNode.appendChild(doc.createTextNode(StringUtil.toXml(text)));
    }

    public static void addCDATA(Document doc, Element parentNode, String content) {
        CDATASection cds = doc.createCDATASection(content == null ? "" : content.replaceAll("\r", ""));
        parentNode.appendChild(cds);
    }

    public static void addCDATA(Document doc, Element node, byte[] bytes) {
        CDATASection cds = doc.createCDATASection(bytes == null ? "" : new BASE64Encoder().encode(bytes));
        node.appendChild(cds);
    }

    public static void addAttribute(Document doc, Element node, String key, String value) {
        Attr attr = doc.createAttribute(key);
        attr.setNodeValue(value == null ? "" : value);
        node.setAttributeNode(attr);
    }

    public static void addIntAttribute(Document doc, Element node, String key, int value) {
        addAttribute(doc, node, key, Integer.toString(value));
    }

    public static void addLongAttribute(Document doc, Element node, String key, long value) {
        addAttribute(doc, node, key, Long.toString(value));
    }

    public static void addBooleanAttribute(Document doc, Element node, String key, boolean value) {
            addAttribute(doc, node, key, Boolean.toString(value));
    }

    public static void addDateAttribute(Document doc, Element node, String key, Date date) {
        if (date!=null)
            addAttribute(doc, node, key, dateFormat.format(date));
    }

    public static void addLocaleAttribute(Document doc, Element node, String key, Locale locale) {
        if (locale!=null)
            addAttribute(doc, node, key, locale.getLanguage());
    }

    public static Document getXmlDocument(String str, String encoding) {
        if (str == null || !str.startsWith("<?xml")) return null;
        Document doc = null;
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(str.getBytes(encoding));
            doc = builder.parse(is);
        } catch (Exception ignored) {
        }
        return doc;
    }

    public static Document getXmlDocumentFromFile(String path) {
        try {
            File xmlFile = new File(path);
            if (!xmlFile.exists()) return null;
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            return builder.parse(xmlFile);
        } catch (Exception e) {
            return null;
        }
    }

    public static String xmlToString(Document doc) {
        try {
            Source source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Element getRootNode(Document doc) {
        return doc.getDocumentElement();
    }

    public static NodeList getChildNodes(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName);
    }

    public static Map<String, String> getAttributes(Node node) {
        Map<String, String> map = new HashMap<>();
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            for (int i = 0; i < attrMap.getLength(); i++) {
                Node attr = attrMap.item(i);
                map.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        return map;
    }

    public static String getStringAttribute(Node node, String key) {
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) return attr.getNodeValue();
        }
        return null;
    }

    public static int getIntAttribute(Node node, String key) {
        int result = -1;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = Integer.parseInt(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public static long getLongAttribute(Node node, String key) {
        long result = -1;
        if (node.hasAttributes()) {
            NamedNodeMap attrMap = node.getAttributes();
            Node attr = attrMap.getNamedItem(key);
            if (attr != null) {
                try {
                    result = Long.parseLong(attr.getNodeValue());
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public static String getCData(Node node) {
        if (node.hasChildNodes()) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child instanceof CDATASection) {
                    return ((CDATASection) child).getData();
                }
                child = child.getNextSibling();
            }
        }
        return "";
    }

    public static Node findSubElement(Node parent, String localName) {
        if (parent == null) {
            return null;
        }
        Node child = parent.getFirstChild();
        while (child != null) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) && (child.getLocalName().equals(localName))) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    public static List<String> getPropertiesFromXML(Node propNode) {
        List<String> properties;
        properties = new ArrayList<>();
        NodeList childList = propNode.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node currentNode = childList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = currentNode.getLocalName();
                String namespace = currentNode.getNamespaceURI();
                properties.add(namespace + ':' + nodeName);
            }
        }
        return properties;
    }
}
