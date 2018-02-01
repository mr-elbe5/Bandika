/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.webbase.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public class HtmlPartData extends PagePartData {

    protected String cssClass = "";
    protected PagePartContent partContent = new PagePartContent();

    public HtmlPartData() {
    }

    public void cloneData(PagePartData data) {
        super.cloneData(data);
        if (data instanceof HtmlPartData) {
            HtmlPartData htmlData = (HtmlPartData) data;
            setCssClass(htmlData.getCssClass());
            copyContent(htmlData);
        }
    }

    public void copyContent(PagePartData part) {
        if (part instanceof HtmlPartData) {
            HtmlPartData htmlData = (HtmlPartData) part;
            partContent.copyContent(htmlData.getPartContent());
        }
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public PagePartContent getPartContent() {
        return partContent;
    }

    public Field ensureField(String name, String fieldType) {
        return getPartContent().ensureField(name, fieldType);
    }

    public void getNodeUsage(Set<Integer> list) {
        partContent.getNodeUsage(list);
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return getPartContent().readPagePartRequestData(request);
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        setCssClass(RequestReader.getString(request, "cssClass"));
    }

    /******************* XML part *********************************/

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        partContent.toXml(xmlDoc, node);
        return node;
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                PagePartContent content = new PagePartContent();
                content.setPagePartId(getId());
                content.fromXml(child);
                partContent = content;
            }
        }
    }

}
