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
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.HashSet;

/**
 * Class ImageField is the data class for editable Fields used as an image area. <br>
 * Usage:
 */
public class ImageField extends BaseField {

  protected int imgId = 0;
  protected int width = 0;
  protected int height = 0;
  protected String altText = "";

  public int getImgId() {
    return imgId;
  }

  public void setImgId(int imgId) {
    this.imgId = imgId;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getAltText() {
    return altText;
  }

  public void setAltText(String altText) {
    this.altText = altText;
  }

  @Override
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    imgId = rdata.getParamInt(getIdentifier() + "ImgId");
    width = rdata.getParamInt(getIdentifier() + "Width");
    height = rdata.getParamInt(getIdentifier() + "Height");
    altText = rdata.getParamString(getIdentifier() + "Alt");
  }

	@Override
	public Element generateXml(Document doc, Element parent){
		Element elem= super.generateXml(doc,parent);
		XmlData.createIntAttribute(doc,elem,"imgId",imgId);
		XmlData.createIntAttribute(doc,elem,"width",width);
		XmlData.createIntAttribute(doc,elem,"height",height);
		XmlData.createCDATA(doc,elem,altText);
		return elem;
	}

	@Override
	public void evaluateXml(Element node){
		super.evaluateXml(node);
		imgId=XmlData.getIntAttribute(node,"imgId");
		width=XmlData.getIntAttribute(node,"width");
		height=XmlData.getIntAttribute(node,"height");
		altText=XmlData.getCData(node);
	}

  @Override
  public void getImageUsage(HashSet<Integer> list) {
    if (imgId!=0)
      list.add(imgId);
  }

}