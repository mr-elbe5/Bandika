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
 * Class IncludeResponse is the response class for including another servlet or jsp <br>
 * Usage:
 */
public class IncludeResponse extends IndirectResponse {

  /**
   * Constructor IncludeResponse creates a new IncludeResponse instance.
   */
  public IncludeResponse() {
  }

  /**
   * Constructor IncludeResponse creates a new IncludeResponse instance.
   *
   * @param url of type String
   */
  public IncludeResponse(String url) {
    super(url);
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return Response.TYPE_INCLUDE;
  }

}
