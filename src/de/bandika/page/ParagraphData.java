/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.BaseData;
import de.bandika.base.RequestError;
import de.bandika.base.XmlData;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;
import de.bandika.page.fields.BaseField;

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

	public BaseField ensureField(String name,String fieldType){
		BaseField field=fields.get(name);
		if (field==null){
			field=BaseField.getNewBaseField(fieldType);
			field.setName(name);
			fields.put(name,field);
		}
		return field;
	}

	@Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    for (BaseField field : fields.values()) {
      field.readRequestData(rdata, sdata, err);
    }
    return err.isEmpty();
  }

	public void generateXml(Document doc,Element parent){
		Element elem=XmlData.createChild(doc,parent,"paragraph");
		XmlData.createIntAttribute(doc,elem,"id",id);
		XmlData.createAttribute(doc,elem,"templateName",templateName);
		for (BaseField field : fields.values()){
			field.generateXml(doc,elem);
		}
	}

	public void evaluateXml(Element node){
		templateName=XmlData.getStringAttribute(node,"templateName");
		fields.clear();
		NodeList fieldNodes=XmlData.getChildNodes(node,"field");
		for (int i=0;i<fieldNodes.getLength();i++){
			Element child=(Element)fieldNodes.item(i);
			String fieldType=XmlData.getStringAttribute(child,"fieldType");
			String name=XmlData.getStringAttribute(child,"name");
			BaseField field=BaseField.getNewBaseField(fieldType);
			field.setName(name);
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
