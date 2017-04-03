/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika._base.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
  public Element generateXml(Document doc, Element parent) {
    Element elem = super.generateXml(doc, parent);
    XmlHelper.createIntAttribute(doc, elem, "imgId", imgId);
    XmlHelper.createIntAttribute(doc, elem, "width", width);
    XmlHelper.createIntAttribute(doc, elem, "height", height);
    XmlHelper.createCDATA(doc, elem, altText);
    return elem;
  }

  @Override
  public void evaluateXml(Element node) {
    super.evaluateXml(node);
    imgId = XmlHelper.getIntAttribute(node, "imgId");
    width = XmlHelper.getIntAttribute(node, "width");
    height = XmlHelper.getIntAttribute(node, "height");
    altText = XmlHelper.getCData(node);
  }

  @Override
  public void getFileUsage(HashSet<Integer> list) {
    if (imgId != 0)
      list.add(imgId);
  }

}