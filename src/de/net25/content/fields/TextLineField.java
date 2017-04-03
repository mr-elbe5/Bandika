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
import de.net25.base.Formatter;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.Locale;

/**
 * Class TextLineField is the data class for editable Fields used for a line of text.  <br>
 * Usage:
 */
public class TextLineField extends BaseField {

  protected String text = "";

  /**
   * Method getText returns the text of this TextLineField object.
   *
   * @return the text (type String) of this TextLineField object.
   */
  public String getText() {
    return text;
  }

  /**
   * Method setText sets the text of this TextLineField object.
   *
   * @param text the text of this TextLineField object.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Method addNodes
   *
   * @param buffer of type StringBuffer
   */
  protected void addNodes(StringBuffer buffer) {
    super.addNodes(buffer);
    XmlData.addNode(buffer, "text", text);
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
    super.readNodes();
    text = XmlData.getNode(xml, "text");
  }

  /**
   * Method getHtml returns the html of this BaseField object.
   *
   * @param locale of type Locale
   * @return the html (type String) of this BaseField object.
   */
  @Override
  public String getHtml(Locale locale) {
    if (text.length() == 0)
      return "&nbsp;";
    return Formatter.toHtml(text);
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  @Override
  public String getEditHtml(Locale locale) {
    StringBuffer buffer = new StringBuffer("<input type=\"text\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("\" value=\"");
    buffer.append(Formatter.toHtml(text));
    buffer.append("\">");
    return buffer.toString();
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   */
  @Override
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    text = rdata.getParamString(getIdentifier());
  }

}