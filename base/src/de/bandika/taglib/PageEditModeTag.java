/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika._base.SessionData;
import de.bandika._base.RequestHelper;
import de.bandika._base.RequestData;
import de.bandika.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class PageEditModeTag extends BaseTag {

  public int doStartTag() throws JspException {
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
    PageData data = (PageData) rdata.getParam("pageData");
    if (data == null) {
      SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
      data = (PageData) sdata.getParam("pageData");
      rdata.setParam("editMode", data != null ? "1" : "0");
    }
    return SKIP_BODY;
  }

}