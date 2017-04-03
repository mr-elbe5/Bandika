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
 * Class HtmlResponse is the response class for returning static HTML code <br>
 * Usage:
 */
public class HtmlResponse extends Response {

  protected String html;

  /**
   * Constructor HtmlResponse creates a new HtmlResponse instance.
   */
  public HtmlResponse() {
  }

  /**
   * Constructor HtmlResponse creates a new HtmlResponse instance.
   *
   * @param html of type String
   */
  public HtmlResponse(String html) {
    this.html = html;
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return Response.TYPE_HTML;
  }

  /**
   * Method getHtml returns the html of this HtmlResponse object.
   *
   * @return the html (type String) of this HtmlResponse object.
   */
  public String getHtml() {
    return html;
  }

  /**
   * Method setHtml sets the html of this HtmlResponse object.
   *
   * @param html the html of this HtmlResponse object.
   */
  public void setHtml(String html) {
    this.html = html;
  }

}
