/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.http;

import de.net25.base.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Class RequestData is the data class for holding request data. <br>
 * Usage:
 */
public class RequestData extends ParamData {

  protected HttpServletRequest request = null;

  /**
   * Method getRequest returns the request of this RequestData object.
   *
   * @return the request (type HttpServletRequest) of this RequestData object.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Method setRequest sets the request of this RequestData object.
   *
   * @param request the request of this RequestData object.
   */
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  /**
   * Method setError sets the error of this RequestData object.
   *
   * @param error the error of this RequestData object.
   */
  public void setError(RequestError error) {
    params.put("error", error);
  }

  /**
   * Method getError returns the error of this RequestData object.
   *
   * @return the error (type RequestError) of this RequestData object.
   */
  public RequestError getError() {
    return (RequestError) params.get("error");
  }

}
