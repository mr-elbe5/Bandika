/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika.data.XmlHelper;
import de.bandika.page.PagePartData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CmsPartData extends PagePartData {

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
        NodeList fieldNodes = XmlHelper.getChildNodes(root, "cms");
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element child = (Element) fieldNodes.item(i);
            String fieldType = XmlHelper.getStringAttribute(child, "fieldType");
            String name = XmlHelper.getStringAttribute(child, "name");
            BaseField field = BaseField.getNewBaseField(fieldType);
            if (field != null) {
                field.setName(name);
                field.evaluateXml(child);
                fields.put(name, field);
            }
        }
    }

    @Override
    public void getDocumentUsage(Set<Integer> list) {
        for (BaseField field : fields.values()) {
            field.getDocumentUsage(list);
        }
    }

    @Override
    public void getImageUsage(Set<Integer> list) {
        for (BaseField field : fields.values()) {
            field.getImageUsage(list);
        }
    }

    public void getPageUsage(Set<Integer> list) {
        for (BaseField field : fields.values()) {
            field.getPageUsage(list);
        }
    }

    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        super.readPagePartRequestData(rdata, sdata);
        boolean complete = true;
        for (BaseField field : getFields().values()) {
            complete &= field.readPagePartRequestData(rdata, sdata);
        }
        return complete;
    }

}
