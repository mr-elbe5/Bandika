/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page.fields;

import de.bandika.base.BaseData;
import de.bandika.base.XmlData;

import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Class BlogField is the data class for entries of a blog. <br>
 * Usage:
 */
public class BlogEntry {

  protected String name = "";
  protected Date time = new Date();
  protected String email = "";
  protected String text = "";

  public BlogEntry() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isComplete() {
    return BaseData.isComplete(name) && BaseData.isComplete(email) && BaseData.isComplete(text);
  }

	public Element generateXml(Document doc, Element parent){
		Element elem= XmlData.createChild(doc,parent,"entry");
		XmlData.createAttribute(doc,elem,"name",name);
		XmlData.createLongAttribute(doc,elem,"time",time.getTime());
		XmlData.createAttribute(doc,elem,"email",email);
		XmlData.createCDATA(doc,elem,text);
		return elem;
	}

	public void evaluateXml(Element node){
		name=XmlData.getStringAttribute(node,"name");
		time=new Date(XmlData.getLongAttribute(node,"time"));
		email=XmlData.getStringAttribute(node,"email");
		text=XmlData.getCData(node);
	}

}