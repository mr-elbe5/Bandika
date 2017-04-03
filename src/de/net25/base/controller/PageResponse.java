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
 * Class PageResponse is a response for a page as template. It includes another content jsp. <br>
 * Usage:
 */
public class PageResponse extends ForwardResponse {

  public static String pageJsp = "/jsps/page.jsp";

  protected String title = null;
  protected String keywords = "";
  protected String jspInclude = null;

  /**
   * Constructor PageResponse creates a new PageResponse instance.
   */
  public PageResponse() {
    super(pageJsp);
  }

  /**
   * Constructor PageResponse creates a new PageResponse instance.
   *
   * @param jsp of type String
   */
  public PageResponse(String jsp) {
    super(pageJsp);
    jspInclude = jsp;
  }

  /**
   * Constructor PageResponse creates a new PageResponse instance.
   *
   * @param title    of type String
   * @param keywords of type String
   * @param jsp      of type String
   */
  public PageResponse(String title, String keywords, String jsp) {
    super(pageJsp);
    this.title = title;
    this.keywords = keywords;
    jspInclude = jsp;
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return TYPE_PAGE;
  }

  /**
   * Method getTitle returns the title of this PageResponse object.
   *
   * @return the title (type String) of this PageResponse object.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Method setTitle sets the title of this PageResponse object.
   *
   * @param title the title of this PageResponse object.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Method getKeywords returns the keywords of this PageResponse object.
   *
   * @return the keywords (type String) of this PageResponse object.
   */
  public String getKeywords() {
    return keywords;
  }

  /**
   * Method setKeywords sets the keywords of this PageResponse object.
   *
   * @param keywords the keywords of this PageResponse object.
   */
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  /**
   * Method getJspInclude returns the jspInclude of this PageResponse object.
   *
   * @return the jspInclude (type String) of this PageResponse object.
   */
  public String getJspInclude() {
    return jspInclude;
  }

  /**
   * Method setJspInclude sets the jspInclude of this PageResponse object.
   *
   * @param jspInclude the jspInclude of this PageResponse object.
   */
  public void setJspInclude(String jspInclude) {
    this.jspInclude = jspInclude;
  }

}
