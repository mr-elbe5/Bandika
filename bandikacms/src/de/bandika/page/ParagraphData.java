/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.*;
import de.bandika.page.fields.BaseField;
import de.bandika.data.BaseData;

import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Class ParagraphData is the data class for a single paragraph in a content page. <br>
 * Usage:
 */
public class ParagraphData extends BaseData {

  protected int pageId;
  protected int ranking = 0;
  protected String templateName = "";

  protected HashMap<String,BaseField> fields = new HashMap<String,BaseField>();

	public int getPageId() {
    return pageId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getTemplateName() {
    return templateName;
  }

  public String getTemplateUrl() {
    return "/_tpl/"+templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public HashMap<String,BaseField> getFields() {
    return fields;
  }

	public BaseField getField(String name){
		return fields.get(name);
	}

	public BaseField ensureField(String name,int fieldType){
		BaseField field=fields.get(name);
		if (field==null){
			field=BaseField.getNewBaseField(fieldType);
			field.setName(name);
			fields.put(name,field);
		}
		return field;
	}

	@Override
  public boolean isComplete() {
    boolean complete=true;
    for (BaseField field : fields.values()) {
      complete &= field.isComplete();
    }
    return complete;
  }

	public void generateXml(Document doc,Element parent){
		Element elem=XmlHelper.createChild(doc,parent,"paragraph");
		XmlHelper.createIntAttribute(doc,elem,"id",id);
		XmlHelper.createAttribute(doc,elem,"templateName",templateName);
		for (BaseField field : fields.values()){
			field.generateXml(doc,elem);
		}
	}

	public void evaluateXml(Element node){
		templateName=XmlHelper.getStringAttribute(node,"templateName");
		fields.clear();
		NodeList fieldNodes=XmlHelper.getChildNodes(node,"field");
		for (int i=0;i<fieldNodes.getLength();i++){
			Element child=(Element)fieldNodes.item(i);
			int fieldType=XmlHelper.getIntAttribute(child,"fieldType");
			String name=XmlHelper.getStringAttribute(child,"name");
			BaseField field=BaseField.getNewBaseField(fieldType);
			field.setName(name);
      field.setFieldType(fieldType);
			field.evaluateXml(child);
			fields.put(name,field);
		}
	}

  public void getDocumentUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getDocumentUsage(list);
    }
  }

  public void getImageUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getImageUsage(list);
    }
  }

}
