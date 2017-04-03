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
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;

import java.util.Locale;

/**
 * Class DocumentField is the data class for editable Fields used as a document link. <br>
 * Usage:
 */
public class DocumentField extends BaseField {

  protected int docId = 0;
  protected String text = "";

  /**
   * Method getDocId returns the docId of this DocumentField object.
   *
   * @return the docId (type int) of this DocumentField object.
   */
  public int getDocId() {
    return docId;
  }

  /**
   * Method setDocId sets the docId of this DocumentField object.
   *
   * @param docId the docId of this DocumentField object.
   */
  public void setDocId(int docId) {
    this.docId = docId;
  }

  /**
   * Method getText returns the text of this DocumentField object.
   *
   * @return the text (type String) of this DocumentField object.
   */
  public String getText() {
    return text;
  }

  /**
   * Method setText sets the text of this DocumentField object.
   *
   * @param text the text of this DocumentField object.
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
    XmlData.addIntNode(buffer, "docId", docId);
    XmlData.addNode(buffer, "text", text);
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
    super.readNodes();
    docId = XmlData.getIntNode(xml, "docId");
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
    StringBuffer buffer = new StringBuffer("<a href=\"");
    if (docId > 0) {
      buffer.append("srv25?ctrl=");
      buffer.append(Statics.KEY_DOCUMENT);
      buffer.append("&method=show&did=");
      buffer.append(docId);
      buffer.append("\"");
    } else {
      buffer.append("#\"");
    }
    buffer.append(" target=\"_blank\">");
    buffer.append(Formatter.toHtml(text));
    buffer.append("</a>");
    return buffer.toString();
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  @Override
  public String getEditHtml(Locale locale) {
    StringBuffer buffer = new StringBuffer("<input type=\"hidden\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("DocId\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("DocId\" value=\"");
    buffer.append(docId);
    buffer.append("\" /><input type=\"text\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("Text\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("Text\" value=\"");
    buffer.append(Formatter.toHtml(text));
    buffer.append("\" />");
    buffer.append("<a href=\"#\" onclick=\"return openSetDocument('");
    buffer.append(getIdentifier());
    buffer.append("');\">");
    buffer.append(Strings.getHtml("document", locale));
    buffer.append("</a>");
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
    docId = rdata.getParamInt(getIdentifier() + "DocId");
    text = rdata.getParamString(getIdentifier() + "Text");
  }

}