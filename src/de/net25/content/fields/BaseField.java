/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content.fields;

import de.net25.base.XmlData;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Class BaseField is the base class for all Field classes used for editing content <br>
 * Usage:
 */
public class BaseField implements Cloneable {

  protected static HashMap<String, Class> baseFieldClasses = new HashMap<String, Class>();

  /**
   * Method addBaseFieldClass
   *
   * @param type of type String
   * @param cls  of type Class
   */
  public static void addBaseFieldClass(String type, Class cls) {
    baseFieldClasses.put(type, cls);
  }

  /**
   * Method getNewBaseField
   *
   * @param type of type String
   * @return BaseField
   */
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

  /**
   * Method getFieldTypes returns the fieldTypes of this BaseField object.
   *
   * @return the fieldTypes (type ArrayList<String>) of this BaseField object.
   */
  public static ArrayList<String> getFieldTypes() {
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(baseFieldClasses.keySet());
    return list;
  }

  protected int paragraphId = 0;
  protected String name;
  protected String fieldType;
  protected String xml;

  /**
   * Method clone
   *
   * @return Object
   * @throws CloneNotSupportedException when data processing is not successful
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Method getParagraphId returns the paragraphId of this BaseField object.
   *
   * @return the paragraphId (type int) of this BaseField object.
   */
  public int getParagraphId() {
    return paragraphId;
  }

  /**
   * Method setParagraphId sets the paragraphId of this BaseField object.
   *
   * @param paragraphId the paragraphId of this BaseField object.
   */
  public void setParagraphId(int paragraphId) {
    this.paragraphId = paragraphId;
  }

  /**
   * Method getName returns the name of this BaseField object.
   *
   * @return the name (type String) of this BaseField object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this BaseField object.
   *
   * @param name the name of this BaseField object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getIdentifier returns the identifier of this BaseField object.
   *
   * @return the identifier (type String) of this BaseField object.
   */
  public String getIdentifier() {
    return Integer.toString(paragraphId) + "_" + name;
  }

  /**
   * Method getFieldType returns the fieldType of this BaseField object.
   *
   * @return the fieldType (type String) of this BaseField object.
   */
  public String getFieldType() {
    return fieldType;
  }

  /**
   * Method setFieldType sets the fieldType of this BaseField object.
   *
   * @param fieldType the fieldType of this BaseField object.
   */
  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  /**
   * Method getXml returns the xml of this BaseField object.
   *
   * @return the xml (type String) of this BaseField object.
   */
  public String getXml() {
    return xml;
  }

  /**
   * Method setXml sets the xml of this BaseField object.
   *
   * @param xml the xml of this BaseField object.
   */
  public void setXml(String xml) {
    this.xml = xml;
  }

  /**
   * Method generateXml
   */
  public void generateXml() {
    StringBuffer buffer = XmlData.startXml();
    addNodes(buffer);
    setXml(XmlData.finishXml(buffer));
  }

  /**
   * Method evaluateXml
   */
  public void evaluateXml() {
    readNodes();
  }

  /**
   * Method addNodes
   *
   * @param buffer of type StringBuffer
   */
  protected void addNodes(StringBuffer buffer) {
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
  }

  /**
   * Method getHtml returns the html of this BaseField object.
   *
   * @param locale of type Locale
   * @return the html (type String) of this BaseField object.
   */
  public String getHtml(Locale locale) {
    return "..";
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  public String getEditHtml(Locale locale) {
    return "::";
  }

  /**
   * Method getDocumentUsage
   *
   * @param list of type HashSet<Integer>
   */
  public void getDocumentUsage(HashSet<Integer> list) {

  }

  /**
   * Method getImageUsage
   *
   * @param list of type HashSet<Integer>
   */
  public void getImageUsage(HashSet<Integer> list) {

  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   */
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
  }

  /**
   * Method isComplete
   *
   * @param s of type String
   * @return boolean
   */
  protected boolean isComplete(String s) {
    return s != null && s.length() > 0;
  }

  /**
   * Method isComplete
   *
   * @param i of type int
   * @return boolean
   */
  protected boolean isComplete(int i) {
    return i != 0;
  }

}
