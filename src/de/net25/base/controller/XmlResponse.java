/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.controller;

/**
 * Class XmlResponse is the response class for returning XML <br>
 * Usage: Take it for AJAX responses.
 */
public class XmlResponse extends Response {

  protected String xml;

  /**
   * Constructor XmlResponse creates a new XmlResponse instance.
   */
  public XmlResponse() {
  }

  /**
   * Constructor XmlResponse creates a new XmlResponse instance.
   *
   * @param xml of type String
   */
  public XmlResponse(String xml) {
    this.xml = xml;
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return Response.TYPE_XML;
  }

  /**
   * Method getXml returns the xml of this XmlResponse object.
   *
   * @return the xml (type String) of this XmlResponse object.
   */
  public String getXml() {
    return xml;
  }

  /**
   * Method setXml sets the xml of this XmlResponse object.
   *
   * @param xml the xml of this XmlResponse object.
   */
  public void setXml(String xml) {
    this.xml = xml;
  }

}
