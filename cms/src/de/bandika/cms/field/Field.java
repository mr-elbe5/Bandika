/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.data.BaseData;
import de.bandika.base.data.XmlData;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Class Field is the base class for all Field classes used for editing
 * content <br>
 * Usage:
 */
public abstract class Field implements Cloneable {

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

    public abstract void appendFieldHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException;

    /******************* XML part *********************************/

    public void createXml(XmlData data, Element parentNode) {
        Element node = data.addNode(parentNode, "field");
        data.addAttribute(node, "fieldType", getFieldType());
        data.addAttribute(node, "name", StringUtil.toXml(getName()));
    }

    public void parseXml(XmlData data, Element node) {
        name = data.getStringAttribute(node, "name");
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
    }

}
