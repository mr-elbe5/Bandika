/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika.data.XmlHelper;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Set;

public class TextLinkField extends BaseField {

    public static String FIELDTYPE_TEXTLINK = "textlink";

    public static void initialize() {
        BaseField.baseFieldClasses.put(FIELDTYPE_TEXTLINK, TextLinkField.class);
    }

    public String getFieldType() {
        return FIELDTYPE_TEXTLINK;
    }

    protected String link = "";
    protected String target = "";
    protected String text = "";

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Element generateXml(Document doc, Element parent) {
        Element elem = super.generateXml(doc, parent);
        XmlHelper.createAttribute(doc, elem, "link", link);
        XmlHelper.createAttribute(doc, elem, "target", target);
        XmlHelper.createCDATA(doc, elem, text);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        link = XmlHelper.getStringAttribute(node, "link");
        target = XmlHelper.getStringAttribute(node, "target");
        text = XmlHelper.getCData(node);
    }

    @Override
    public void addSearchContent(StringBuffer buffer) {
        buffer.append(' ').append(text).append(' ');
    }

    @Override
    public void getDocumentUsage(Set<Integer> list) {
        String fileLink = "/document.srv?method=show&fid=";
        int pos = link.indexOf(fileLink);
        if (pos != -1) {
            pos += fileLink.length();
            int fid = 0;
            try {
                fid = Integer.parseInt(fileLink.substring(pos));
            } catch (Exception ignore) {
            }
            if (fid > 0)
                list.add(fid);
        }
    }

    @Override
    public void getImageUsage(Set<Integer> list) {
        String fileLink = "/image.srv?method=show&fid=";
        int pos = link.indexOf(fileLink);
        if (pos != -1) {
            pos += fileLink.length();
            int fid = 0;
            try {
                fid = Integer.parseInt(fileLink.substring(pos));
            } catch (Exception ignore) {
            }
            if (fid > 0)
                list.add(fid);
        }
    }

    @Override
    public void getPageUsage(Set<Integer> list) {
        String pageLink = "/page.srv?method=show&id=";
        int pos = link.indexOf(pageLink);
        if (pos != -1) {
            pos += pageLink.length();
            int fid = 0;
            try {
                fid = Integer.parseInt(pageLink.substring(pos));
            } catch (Exception ignore) {
            }
            if (fid > 0)
                list.add(fid);
        }
    }

    @Override
    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        String ident = getIdentifier();
        setLink(rdata.getString(ident + "Link"));
        setTarget(rdata.getString(ident + "Target"));
        setText(rdata.getString(ident + "Text"));
        return isComplete(sdata);
    }

}
