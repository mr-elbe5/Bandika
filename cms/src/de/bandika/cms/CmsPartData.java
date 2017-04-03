/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika._base.XmlHelper;
import de.bandika.page.PagePartData;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.HashMap;

public class CmsPartData extends PagePartData {

  public static final String DATAKEY = "data|cmspart";

  protected HashMap<String, BaseField> fields = new HashMap<String, BaseField>();

  public HashMap<String, BaseField> getFields() {
    return fields;
  }

  public BaseField getField(String name) {
    return fields.get(name);
  }

  public BaseField ensureField(String name, int fieldType) {
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
      int fieldType = XmlHelper.getIntAttribute(child, "fieldType");
      String name = XmlHelper.getStringAttribute(child, "name");
      BaseField field = BaseField.getNewBaseField(fieldType);
      field.setName(name);
      field.setFieldType(fieldType);
      field.evaluateXml(child);
      fields.put(name, field);
    }
  }

  @Override
  public void addSearchContent(StringBuffer buffer) {
    for (BaseField field : fields.values()) {
      field.addSearchContent(buffer);
    }
  }

  @Override
  public void getFileUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getFileUsage(list);
    }
  }

  public void getPageUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getPageUsage(list);
    }
  }

  @Override
  public boolean isComplete() {
    boolean complete = true;
    for (BaseField field : fields.values()) {
      complete &= field.isComplete();
    }
    return complete;
  }

  public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
    boolean complete = true;
    for (BaseField field : getFields().values()) {
      int fieldType = field.getFieldType();
      switch (fieldType) {
        case BaseField.FIELDTYPE_TEXTLINE: {
          TextLineField fld = (TextLineField) field;
          fld.setText(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_TEXTAREA: {
          TextAreaField fld = (TextAreaField) field;
          fld.setText(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_HTML: {
          HtmlField fld = (HtmlField) field;
          fld.setHtml(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_IMAGE: {
          ImageField fld = (ImageField) field;
          String ident = fld.getIdentifier();
          fld.setImgId(rdata.getParamInt(ident + "ImgId"));
          fld.setWidth(rdata.getParamInt(ident + "Width"));
          fld.setHeight(rdata.getParamInt(ident + "Height"));
          fld.setAltText(rdata.getParamString(ident + "Alt"));
        }
        break;
        case BaseField.FIELDTYPE_LINK: {
          LinkField fld = (LinkField) field;
          String ident = fld.getIdentifier();
          fld.setLink(rdata.getParamString(ident + "Link"));
          fld.setTarget(rdata.getParamString(ident + "Target"));
          fld.setText(rdata.getParamString(ident + "Text"));
        }
        break;
      }
      complete &= field.isComplete();
    }
    return complete;
  }

}