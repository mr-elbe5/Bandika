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
 * Class PopupResponse is the response class for showing a popup page. It includes another content jsp. <br>
 * Usage:
 */
public class PopupResponse extends ForwardResponse {

  public static String pageJsp = "/jsps/popup.jsp";

  protected String title = null;
  protected String jspInclude = null;

  /**
   * Constructor PopupResponse creates a new PopupResponse instance.
   */
  public PopupResponse() {
    super(pageJsp);
  }

  /**
   * Constructor PopupResponse creates a new PopupResponse instance.
   *
   * @param jsp of type String
   */
  public PopupResponse(String jsp) {
    super(pageJsp);
    jspInclude = jsp;
  }

  /**
   * Constructor PopupResponse creates a new PopupResponse instance.
   *
   * @param title of type String
   * @param jsp   of type String
   */
  public PopupResponse(String title, String jsp) {
    super(pageJsp);
    this.title = title;
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
   * Method getTitle returns the title of this PopupResponse object.
   *
   * @return the title (type String) of this PopupResponse object.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Method setTitle sets the title of this PopupResponse object.
   *
   * @param title the title of this PopupResponse object.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Method getJspInclude returns the jspInclude of this PopupResponse object.
   *
   * @return the jspInclude (type String) of this PopupResponse object.
   */
  public String getJspInclude() {
    return jspInclude;
  }

  /**
   * Method setJspInclude sets the jspInclude of this PopupResponse object.
   *
   * @param jspInclude the jspInclude of this PopupResponse object.
   */
  public void setJspInclude(String jspInclude) {
    this.jspInclude = jspInclude;
  }

}