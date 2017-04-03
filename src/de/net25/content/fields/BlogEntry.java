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
import de.net25.base.BaseData;

import java.util.Date;

/**
 * Class BlogField is the data class for entries of a blog. <br>
 * Usage:
 */
public class BlogEntry {

  protected String name = "";
  protected Date time = new Date();
  protected String email = "";
  protected String text = "";

  /**
   * Constructor BlogEntry creates a new BlogEntry instance.
   */
  public BlogEntry() {
  }

  /**
   * Method getName returns the name of this BlogEntry object.
   *
   * @return the name (type String) of this BlogEntry object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this BlogEntry object.
   *
   * @param name the name of this BlogEntry object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getTime returns the time of this BlogEntry object.
   *
   * @return the time (type Date) of this BlogEntry object.
   */
  public Date getTime() {
    return time;
  }

  /**
   * Method setTime sets the time of this BlogEntry object.
   *
   * @param time the time of this BlogEntry object.
   */
  public void setTime(Date time) {
    this.time = time;
  }

  /**
   * Method getEmail returns the email of this BlogEntry object.
   *
   * @return the email (type String) of this BlogEntry object.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Method setEmail sets the email of this BlogEntry object.
   *
   * @param email the email of this BlogEntry object.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Method getText returns the text of this BlogEntry object.
   *
   * @return the text (type String) of this BlogEntry object.
   */
  public String getText() {
    return text;
  }

  /**
   * Method setText sets the text of this BlogEntry object.
   *
   * @param text the text of this BlogEntry object.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Method addNode ...
   *
   * @param buffer of type StringBuffer
   * @param idx    of type int
   */
  protected void addNode(StringBuffer buffer, int idx) {
    XmlData.addNode(buffer, "entry" + idx, name);
    XmlData.addLongNode(buffer, "time" + idx, time.getTime());
    XmlData.addNode(buffer, "email" + idx, email);
    XmlData.addNode(buffer, "text" + idx, text);
  }

  /**
   * Method readNode ...
   *
   * @param xml of type string
   * @param idx of type int
   */
  protected void readNode(String xml, int idx) {
    name = XmlData.getNode(xml, "entry" + idx);
    time = new Date(XmlData.getLongNode(xml, "time" + idx));
    email = XmlData.getNode(xml, "email" + idx);
    text = XmlData.getNode(xml, "text" + idx);
  }

  /**
   * Method isComplete returns the complete of this BlogEntry object.
   *
   * @return the complete (type boolean) of this BlogEntry object.
   */
  public boolean isComplete() {
    return BaseData.isComplete(name) && BaseData.isComplete(email) && BaseData.isComplete(text);
  }

}