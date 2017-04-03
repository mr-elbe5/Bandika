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
 * Class IndirectResponse is the base class for including or forwarding a response <br>
 * Usage:
 */
public abstract class IndirectResponse extends Response {

  protected String url = null;

  /**
   * Constructor IndirectResponse creates a new IndirectResponse instance.
   */
  public IndirectResponse() {
  }

  /**
   * Constructor IndirectResponse creates a new IndirectResponse instance.
   *
   * @param url of type String
   */
  public IndirectResponse(String url) {
    this.url = url;
  }

  /**
   * Method isDirectResponse returns the directResponse of this Response object.
   *
   * @return the directResponse (type boolean) of this Response object.
   */
  public boolean isDirectResponse() {
    return false;
  }

  /**
   * Method getUrl returns the url of this IndirectResponse object.
   *
   * @return the url (type String) of this IndirectResponse object.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Method setUrl sets the url of this IndirectResponse object.
   *
   * @param url the url of this IndirectResponse object.
   */
  public void setUrl(String url) {
    this.url = url;
  }

}
