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

import java.util.Locale;
import java.util.ArrayList;

/**
 * Class BlogField is the data class for editable Fields used for a blog. <br>
 * Usage:
 */
public class BlogField extends BaseField {

  protected ArrayList<BlogEntry> entries = new ArrayList<BlogEntry>();

  /**
   * Method addEntry ...
   *
   * @param entry of type BlogEntry
   */
  public void addEntry(BlogEntry entry) {
    entries.add(entry);
  }

  /**
   * Method getEntries returns the entries of this BlogField object.
   *
   * @return the entries (type ArrayList<BlogEntry>) of this BlogField object.
   */
  public ArrayList<BlogEntry> getEntries() {
    return entries;
  }

  /**
   * Method addNodes
   *
   * @param buffer of type StringBuffer
   */
  protected void addNodes(StringBuffer buffer) {
    super.addNodes(buffer);
    XmlData.addIntNode(buffer, "numEntries", entries.size());
    for (int i = 0; i < entries.size(); i++)
      entries.get(i).addNode(buffer, i);
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
    super.readNodes();
    int numEntries = XmlData.getIntNode(xml, "numEntries");
    for (int i = 0; i < numEntries; i++) {
      BlogEntry entry = new BlogEntry();
      entry.readNode(xml, i);
      entries.add(entry);
    }
  }

  /**
   * Method getHtml returns the html of this BaseField object.
   *
   * @param locale of type Locale
   * @return the html (type String) of this BaseField object.
   */
  @Override
  public String getHtml(Locale locale) {
    return "";
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  @Override
  public String getEditHtml(Locale locale) {
    return "";
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

  }

}