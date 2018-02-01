/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.data.BaseData;
import de.bandika.base.data.XmlData;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.field.Fields;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PagePartContent extends BaseData implements XmlData {

    protected int pagePartId = 0;
    protected int idx = 0;
    protected String content = "";
    protected Map<String, Field> fields = new HashMap<>();

    public PagePartContent() {
    }

    public void copyContent(PagePartContent part) {
        setIdx(part.getIdx());
        setContent(part.getContent());
    }

    public int getPagePartId() {
        return pagePartId;
    }

    public void setPagePartId(int pagePartId) {
        this.pagePartId = pagePartId;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public Field ensureField(String name, String fieldType) {
        Field field = fields.get(name);
        if (field == null) {
            field = Fields.getNewField(fieldType);
            assert field != null;
            field.setName(name);
            field.setPagePartId(getPagePartId());
            fields.put(name, field);
        }
        return field;
    }

    public void getNodeUsage(Set<Integer> list) {
        for (Field field : fields.values()) {
            field.getNodeUsage(list);
        }
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        boolean complete = true;
        for (Field field : getFields().values()) {
            complete &= field.readPagePartRequestData(request);
        }
        return complete;
    }

    /******************* XML part *********************************/

    public void addXmlAttributes(Document xmlDoc, Element node) {
        XmlUtil.addIntAttribute(xmlDoc, node, "idx", getIdx());
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "partContent");
        addXmlAttributes(xmlDoc, node);
        for (Field field : fields.values()) {
            field.toXml(xmlDoc, node);
        }
        return node;
    }

    public void getXmlAttributes(Element node) {
        setIdx(XmlUtil.getIntAttribute(node, "idx"));
    }

    public void fromXml(Element node) {
        getXmlAttributes(node);
        fields.clear();
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("field")) {
                String fieldType = XmlUtil.getStringAttribute(child, "fieldType");
                Field field = Fields.getNewField(fieldType);
                if (field != null) {
                    field.fromXml(child);
                    fields.put(field.getName(), field);
                }
            }
        }
    }

}
