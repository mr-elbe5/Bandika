/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.data.BaseData;
import de.bandika.base.data.XmlData;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.template.TemplateAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Class Field is the base class for all Field classes used for editing
 * content <br>
 * Usage:
 */
public abstract class Field implements Cloneable, XmlData {

    protected int pagePartId = 0;
    protected String name = "";

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setPagePartId(int pagePartId) {
        this.pagePartId = pagePartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return Integer.toString(pagePartId) + '_' + name;
    }

    public abstract String getFieldType();

    protected boolean isComplete(String s) {
        return s != null && s.length() > 0;
    }

    protected boolean isComplete(int i) {
        return i != 0;
    }

    public boolean isComplete() {
        return BaseData.isComplete(name);
    }

    public void getNodeUsage(Set<Integer> list) {
    }

    public abstract boolean readPagePartRequestData(HttpServletRequest request);

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

    public boolean isNullOrEmpty(String src) {
        return StringUtil.isNullOrEmpty(src);
    }

    /******************* HTML part *********************************/

    public abstract void appendFieldHtml(StringBuilder sb, TemplateAttributes attributes, String content, PagePartData partData, PageData pageData, HttpServletRequest request);

    public abstract void appendFieldHtml(JspWriter writer, TemplateAttributes attributes, String content, PagePartData partData, PageData pageData, HttpServletRequest request) throws IOException;

    /******************* XML part *********************************/

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "field");
        addXmlAttributes(xmlDoc, node);
        return node;
    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
        XmlUtil.addAttribute(xmlDoc, node, "fieldType", getFieldType());
        XmlUtil.addAttribute(xmlDoc, node, "name", StringUtil.toXml(getName()));
    }

    public void fromXml(Element node) {
        name = XmlUtil.getStringAttribute(node, "name");
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
    }

}
