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
 * Class Response is the base class for all controller responses<br>
 * Usage: returned from Controller methods which are called from the servlet.
 * Evaluated by StdServlet
 */
public abstract class Response {

  public static final int TYPE_BINARY = 0;
  public static final int TYPE_HTML = 1;
  public static final int TYPE_INCLUDE = 2;
  public static final int TYPE_FORWARD = 3;
  public static final int TYPE_PAGE = 4;
  public static final int TYPE_XML = 5;

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public abstract int getType();

  /**
   * Method isDirectResponse returns the directResponse of this Response object.
   *
   * @return the directResponse (type boolean) of this Response object.
   */
  public boolean isDirectResponse() {
    return true;
  }

  /**
   * Method isSticky returns the sticky of this Response object.
   *
   * @return the sticky (type boolean) of this Response object.
   */
  public boolean isSticky() {
    return false;
  }

}
