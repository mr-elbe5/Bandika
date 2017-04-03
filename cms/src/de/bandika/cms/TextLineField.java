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

/**
 * Class TextLineField is the data class for editable Fields used for a line of
 * text. <br>
 * Usage:
 */
public class TextLineField extends BaseField {

  protected String text = "";

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public Element generateXml(Document doc, Element parent) {
    Element elem = super.generateXml(doc, parent);
    XmlHelper.createCDATA(doc, elem, text);
    return elem;
  }

  @Override
  public void evaluateXml(Element node) {
    super.evaluateXml(node);
    text = XmlHelper.getCData(node);
  }

  @Override
  public void addSearchContent(StringBuffer buffer) {
    buffer.append(" ").append(text).append(" ");
  }

}