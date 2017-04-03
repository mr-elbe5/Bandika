/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.util.HashMap;

/**
 * Class XmlHelper is a helper for simple XML exchange. <br>
 * Usage:
 */
public class XmlHelper {

  public static Document createXmlDocument(){
    try{
      DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = fact.newDocumentBuilder();
      Document doc=builder.newDocument();
      doc.setXmlVersion("1.0");
      return doc;
    }
    catch (Exception e){
      return null;
    }
  }

  public static Element createRootNode(Document doc, String rootName){
    Element rootElem=doc.createElement(rootName);
    doc.appendChild(rootElem);
    return rootElem;
  }

  public static Element createChild(Document doc, Element parentNode, String name){
    Element rootElem=doc.createElement(name);
    parentNode.appendChild(rootElem);
    return rootElem;
  }

  public static CDATASection createCDATA(Document doc, Element parentNode, String content){
    CDATASection cds=doc.createCDATASection(content);
    parentNode.appendChild(cds);
    return cds;
  }

  public static Attr createAttribute(Document doc, Element parentNode, String key, String value){
    Attr attr=doc.createAttribute(key);
    attr.setNodeValue(value);
    parentNode.setAttributeNode(attr);
    return attr;
  }

	public static Attr createIntAttribute(Document doc, Element parentNode, String key, int value){
    return createAttribute(doc,parentNode,key,Integer.toString(value));
  }

	public static Attr createLongAttribute(Document doc, Element parentNode, String key, long value){
    return createAttribute(doc,parentNode,key,Long.toString(value));
  }

	public static Attr createBooleanAttribute(Document doc, Element parentNode, String key, boolean value){
    return createAttribute(doc,parentNode,key,Boolean.toString(value));
  }

  public static Document getXmlDocument(String str){
		if (str==null || !str.startsWith("<?xml"))
		  return null;
		Document doc=null;
    try{
      DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = fact.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
			doc=builder.parse(is);
    }
    catch (Exception ignored){
    }
		return doc;
  }

  public static Document getXmlDocumentFromFile(String path){
    try{
      File xmlFile = new File(path);
      if (!xmlFile.exists())
        return null;
      DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = fact.newDocumentBuilder();
      return builder.parse(xmlFile);
    }
    catch (Exception e){
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
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}


  public static Element getRootNode(Document doc){
    return doc.getDocumentElement();
  }

	public static NodeList getChildNodes(Element parent,String tagName){
    return parent.getElementsByTagName(tagName);
  }

  public static HashMap<String,String> getAttributes(Node node){
    HashMap<String,String> map=new HashMap<String,String>();
    if (node.hasAttributes()){
      NamedNodeMap attrMap=node.getAttributes();
      for (int i=0;i<attrMap.getLength();i++){
        Node attr=attrMap.item(i);
        map.put(attr.getNodeName(),attr.getNodeValue());
      }
    }
    return map;
  }

  public static String getStringAttribute(Node node,String key){
    if (node.hasAttributes()){
      NamedNodeMap attrMap=node.getAttributes();
      Node attr=attrMap.getNamedItem(key);
      if (attr!=null)
        return attr.getNodeValue();
    }
    return null;
  }

  public static int getIntAttribute(Node node,String key){
    int result=-1;
    if (node.hasAttributes()){
      NamedNodeMap attrMap=node.getAttributes();
      Node attr=attrMap.getNamedItem(key);
      if (attr!=null){
        try{
          result=Integer.parseInt(attr.getNodeValue());
        }
        catch (Exception ignored){}
      }
    }
    return result;
  }

	public static long getLongAttribute(Node node,String key){
    long result=-1;
    if (node.hasAttributes()){
      NamedNodeMap attrMap=node.getAttributes();
      Node attr=attrMap.getNamedItem(key);
      if (attr!=null){
        try{
          result=Long.parseLong(attr.getNodeValue());
        }
        catch (Exception ignored){}
      }
    }
    return result;
  }

  public static String getCData(Node node){
		if (node.hasChildNodes()){
			Node child=node.getFirstChild();
			while(child!=null){
    		if (child instanceof CDATASection)
      		return ((CDATASection)child).getData();
				child=child.getNextSibling();
			}
		}
    return "";
  }


}
