/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika.data.BaseData;
import de.bandika.data.XmlHelper;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Class BaseField is the base class for all Field classes used for editing
 * content <br>
 * Usage:
 */
public abstract class BaseField implements Cloneable {

    protected static Map<String, Class<?>> baseFieldClasses = new HashMap<>();

    public static BaseField getNewBaseField(String type) {
        Class<?> cls = baseFieldClasses.get(type);
        try {
            if (cls != null)
                return (BaseField) cls.newInstance();
        } catch (Exception ignore) {
        }
        return null;
    }

    public static List<String> getFieldTypes() {
        List<String> list = new ArrayList<>();
        list.addAll(baseFieldClasses.keySet());
        return list;
    }

    protected int pagePartId = 0;
    protected String name = "";

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getPagePartId() {
        return pagePartId;
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

    public boolean isComplete(SessionData sdata) {
        return BaseData.isComplete(name);
    }

    public Element generateXml(Document doc, Element parent) {
        Element elem = XmlHelper.createChild(doc, parent, "cms");
        XmlHelper.createAttribute(doc, elem, "fieldType", getFieldType());
        XmlHelper.createAttribute(doc, elem, "name", name);
        return elem;
    }

    public void evaluateXml(Element node) {
        name = XmlHelper.getStringAttribute(node, "name");
    }

    public void addSearchContent(StringBuffer buffer) {
    }

    public void getDocumentUsage(Set<Integer> list) {
    }

    public void getImageUsage(Set<Integer> list) {
    }

    public void getPageUsage(Set<Integer> list) {
    }

    public abstract boolean readPagePartRequestData(RequestData rdata, SessionData sdata);

}
