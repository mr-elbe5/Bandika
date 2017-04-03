/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.template;

import de.net25.base.*;
import de.net25.http.RequestData;

/**
 * Class TemplateData is the data class for paragraph templates. <br>
 * Usage:
 */
public class TemplateData extends BaseData {

  protected String description;
  protected String html;

  /**
   * Method getDescription returns the description of this TemplateData object.
   *
   * @return the description (type String) of this TemplateData object.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Method getName returns the name of this TemplateData object.
   *
   * @return the name (type String) of this TemplateData object.
   */
  public String getName() {
    return getDescription();
  }

  /**
   * Method setDescription sets the description of this TemplateData object.
   *
   * @param description the description of this TemplateData object.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Method getHtml returns the html of this TemplateData object.
   *
   * @return the html (type String) of this TemplateData object.
   */
  public String getHtml() {
    return html;
  }

  /**
   * Method setHtml sets the html of this TemplateData object.
   *
   * @param html the html of this TemplateData object.
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param err   of type RequestError
   * @return boolean
   */
  public boolean readRequestData(RequestData rdata, RequestError err) {
    description = rdata.getParamString("description");
    html = rdata.getParamString("html");
    return true;
  }

}