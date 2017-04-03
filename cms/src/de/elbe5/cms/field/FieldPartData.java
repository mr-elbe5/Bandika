/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.base.util.XmlUtil;
import de.elbe5.cms.page.PagePartData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldPartData extends PagePartData {
    protected Map<String, BaseField> fields = new HashMap<>();

    public Map<String, BaseField> getFields() {
        return fields;
    }

    public BaseField getField(String name) {
        return fields.get(name);
    }

    public BaseField ensureField(String name, String fieldType) {
        BaseField field = fields.get(name);
        if (field == null) {
            field = BaseField.getNewBaseField(fieldType);
            field.setName(name);
            field.setPagePartId(getId());
            fields.put(name, field);
        }
        return field;
    }

    @Override
    public void generateContentXml(Document doc, Element root) {
        for (BaseField field : fields.values()) {
            field.generateXml(doc, root);
        }
    }

    @Override
    public void evaluateContentXml(Element root) {
        fields.clear();
        NodeList fieldNodes = XmlUtil.getChildNodes(root, "cms");
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element child = (Element) fieldNodes.item(i);
            String fieldType = XmlUtil.getStringAttribute(child, "fieldType");
            String name = XmlUtil.getStringAttribute(child, "name");
            BaseField field = BaseField.getNewBaseField(fieldType);
            if (field != null) {
                field.setName(name);
                field.evaluateXml(child);
                fields.put(name, field);
            }
        }
    }

    @Override
    public void getFileUsage(Set<Integer> list) {
        for (BaseField field : fields.values()) {
            field.getFileUsage(list);
        }
    }

    public void getPageUsage(Set<Integer> list) {
        for (BaseField field : fields.values()) {
            field.getPageUsage(list);
        }
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        super.readPagePartRequestData(request);
        boolean complete = true;
        for (BaseField field : getFields().values()) {
            complete &= field.readPagePartRequestData(request);
        }
        return complete;
    }
}
