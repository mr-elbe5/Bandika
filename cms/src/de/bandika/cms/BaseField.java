/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika._base.XmlHelper;
import de.bandika._base.DataHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class BaseField is the base class for all Field classes used for editing
 * content <br>
 * Usage:
 */
public class BaseField implements Cloneable {

  public static final int FIELDTYPE_TEXTLINE = 1;
  public static final int FIELDTYPE_TEXTAREA = 2;
  public static final int FIELDTYPE_HTML = 3;
  public static final int FIELDTYPE_IMAGE = 4;
  public static final int FIELDTYPE_LINK = 5;
  public static final int FIELDTYPE_BLOG = 6;
  public static final int FIELDTYPE_MAIL = 7;

  protected static HashMap<Integer, Class<?>> baseFieldClasses = new HashMap<Integer, Class<?>>();

  static {
    try {
      baseFieldClasses.put(FIELDTYPE_TEXTLINE, Class.forName("de.bandika.cms.TextLineField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_TEXTAREA, Class.forName("de.bandika.cms.TextAreaField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_HTML, Class.forName("de.bandika.cms.HtmlField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_IMAGE, Class.forName("de.bandika.cms.ImageField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_LINK, Class.forName("de.bandika.cms.LinkField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_BLOG, Class.forName("de.bandika.cms.BlogField"));
    } catch (Exception ignore) {
    }
    try {
      baseFieldClasses.put(FIELDTYPE_MAIL, Class.forName("de.bandika.cms.MailField"));
    } catch (Exception ignore) {
    }
  }

  public static BaseField getNewBaseField(int type) {
    Class<?> cls = baseFieldClasses.get(type);
    try {
      BaseField field = (BaseField) cls.newInstance();
      field.setFieldType(type);
      return field;
    } catch (Exception e) {
      return null;
    }
  }

  public static ArrayList<Integer> getFieldTypes() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.addAll(baseFieldClasses.keySet());
    return list;
  }

  protected int pagePartId = 0;
  protected String name = "";
  protected int fieldType;

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
    return Integer.toString(pagePartId) + "_" + name;
  }

  public int getFieldType() {
    return fieldType;
  }

  public void setFieldType(int fieldType) {
    this.fieldType = fieldType;
  }

  protected boolean isComplete(String s) {
    return s != null && s.length() > 0;
  }

  protected boolean isComplete(int i) {
    return i != 0;
  }

  public boolean isComplete() {
    return DataHelper.isComplete(name);
  }

  public Element generateXml(Document doc, Element parent) {
    Element elem = XmlHelper.createChild(doc, parent, "cms");
    XmlHelper.createIntAttribute(doc, elem, "fieldType", fieldType);
    XmlHelper.createAttribute(doc, elem, "name", name);
    return elem;
  }

  public void evaluateXml(Element node) {
    name = XmlHelper.getStringAttribute(node, "name");
  }

  public void addSearchContent(StringBuffer buffer) {
  }

  public void getFileUsage(HashSet<Integer> list) {
  }

  public void getPageUsage(HashSet<Integer> list) {
  }

}
