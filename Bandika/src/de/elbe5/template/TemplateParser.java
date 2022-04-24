package de.elbe5.template;

import de.elbe5.base.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TemplateParser extends DefaultHandler2 {

    private final String code;
    private final Template template = new Template();
    private final List<TemplateTag> tagStack = new ArrayList<>();

    private StringBuilder buffer = new StringBuilder();
    private boolean tagIsOpen = false;

    public TemplateParser(String code){
        this.code = code;
        tagStack.add(template);
    }

    public Template parse(){
        tagIsOpen = false;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(new InputSource(new StringReader(code)),this);
            return template;
        }
        catch (Exception e){
            Log.error("parse error", e);
            return null;
        }
    }

    private void pushTag(String type, Attributes attr){
        TemplateTag tag;
        tag = TemplateTagFactory.createTag(type);
        addChildNode(tag);
        tagStack.add(tag);
        for(int i=0; i<attr.getLength(); i++){
            tag.getAttributes().put(attr.getQName(i),attr.getValue(i));
        }
    }

    private void popTag(String type) throws SAXException{
        if (tagStack.size()<2){
            throw new SAXException("bad closing tag: " + type);
        }
        if (!tagStack.get(tagStack.size()-1).getType().equals(type)){
            throw new SAXException("bad closing tag: " + type + " does not match " + tagStack.get(tagStack.size()-1).getType());
        }
        tagStack.remove(tagStack.size()-1);
    }

    private void addChildNode(TemplateNode node){
        tagStack.get(tagStack.size()-1).addChildNode(node);
    }

    public void startDTD(String name,  String publicId, String systemId) {
        template.setDocType(name);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!localName.equals(qName) && qName.startsWith("spg:")) {
            closeOpenTag();
            flushBuffer();
            pushTag(localName, attributes);
        }
        else {
            buffer.append("<").append(qName);
            for (int i=0; i<attributes.getLength(); i++){
                buffer.append(" ").append(attributes.getQName(i)).append("=\"").append(attributes.getValue(i)).append("\"");
            }
            tagIsOpen = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!localName.equals(qName) && qName.startsWith("spg:")) {
            closeOpenTag();
            flushBuffer();
            popTag(localName);
        }
        else {
            if (tagIsOpen){
                buffer.append("/>");
            }
            else {
                buffer.append("</").append(qName).append(">");
            }
            tagIsOpen = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        closeOpenTag();
        buffer.append(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        flushBuffer();
    }

    private void closeOpenTag(){
        if (tagIsOpen){
            buffer.append(">");
            tagIsOpen = false;
        }
    }

    private void flushBuffer(){
        if (!buffer.isEmpty()){
            addChildNode(new TemplateCode(buffer.toString()));
            buffer = new StringBuilder();
        }
    }

}
