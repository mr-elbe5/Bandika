/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page.fields;

import de.bandika.base.RequestError;
import de.bandika.base.XmlData;
import de.bandika.base.BaseData;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class BaseField is the base class for all Field classes used for editing content <br>
 * Usage:
 */
public class BaseField implements Cloneable {

  protected static HashMap<String, Class> baseFieldClasses = new HashMap<String, Class>();

  public static void addBaseFieldClass(String type, Class cls) {
    baseFieldClasses.put(type, cls);
  }

  public static BaseField getNewBaseField(String type) {
    Class cls = baseFieldClasses.get(type);
    try {
      BaseField field = (BaseField) cls.newInstance();
      field.setFieldType(type);
      return field;
    }
    catch (Exception e) {
      return null;
    }
  }

  public static ArrayList<String> getFieldTypes() {
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(baseFieldClasses.keySet());
    return list;
  }

  protected int paragraphId = 0;
  protected String name="";
  protected String fieldType;

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public int getParagraphId() {
    return paragraphId;
  }

  public void setParagraphId(int paragraphId) {
    this.paragraphId = paragraphId;
  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentifier() {
    return Integer.toString(paragraphId) + "_" + name;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
  }

  protected boolean isComplete(String s) {
    return s != null && s.length() > 0;
  }

  protected boolean isComplete(int i) {
    return i != 0;
  }

	public boolean isComplete() {
    return BaseData.isComplete(name);
  }

	public Element generateXml(Document doc, Element parent){
		Element elem= XmlData.createChild(doc,parent,"field");
		XmlData.createAttribute(doc,elem,"fieldType",fieldType);
		XmlData.createAttribute(doc,elem,"name",name);
		return elem;
	}

	public void evaluateXml(Element node){
    name=XmlData.getStringAttribute(node,"name");
	}

  public void getDocumentUsage(HashSet<Integer> list) {
  }

  public void getImageUsage(HashSet<Integer> list) {
  }

}
