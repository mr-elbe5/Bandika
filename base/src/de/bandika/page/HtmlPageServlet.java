/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.BaseServlet;
import de.bandika._base.Controller;
import de.bandika._base.Logger;
import de.bandika._base.RequestData;

import javax.servlet.http.HttpServletRequest;

/**
 * Class HtmlPageServlet is the servlet class for receiving requests and returning
 * responses for html named requests. <br>
 * Usage:
 */
public class HtmlPageServlet extends BaseServlet {

  private static final long serialVersionUID = 1L;

  private static String SUFFIX = ".html";
  private int SUFFIX_LENGTH = SUFFIX.length();

  protected Controller getController() {
    return PageController.getInstance();
  }

  @Override
  protected void adjustRequestData(HttpServletRequest request, RequestData rdata) {
    String uri = request.getRequestURI();
    int id = 0;
    if (uri.endsWith(SUFFIX)) {
      int pos = uri.lastIndexOf(".", uri.length() - SUFFIX_LENGTH - 1);
      if (pos != -1) {
        try {
          id = Integer.parseInt(uri.substring(pos + 1, uri.length() - SUFFIX_LENGTH));
        } catch (Exception e) {
          Logger.warn(getClass(), "could not find id in page name");
        }
      }
      if (id != 0)
        rdata.setParam("id", Integer.toString(id));
    }
  }

}