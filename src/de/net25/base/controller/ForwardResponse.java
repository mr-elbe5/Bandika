/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.controller;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class ForwardResponse is the base class for responses forwarding to some other servlet or jsp <br>
 * Usage:
 */
public class ForwardResponse extends IndirectResponse {

  /**
   * Constructor ForwardResponse creates a new ForwardResponse instance.
   */
  public ForwardResponse() {
  }

  /**
   * Constructor ForwardResponse creates a new ForwardResponse instance.
   *
   * @param url of type String
   */
  public ForwardResponse(String url) {
    super(url);
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return Response.TYPE_FORWARD;
  }

  /**
   * Method isSticky returns the sticky of this Response object.
   *
   * @return the sticky (type boolean) of this Response object.
   */
  public boolean isSticky() {
    return true;
  }

  /**
   * Method process
   *
   * @param sc       of type ServletContext
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws IOException      when data processing is not successful
   * @throws ServletException when data processing is not successful
   */
  public void process(ServletContext sc, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    RequestDispatcher rd = sc.getRequestDispatcher(url);
    if (rd == null)
      throw new ServletException("url does not exist: " + url);
    rd.forward(request, response);
  }

}
