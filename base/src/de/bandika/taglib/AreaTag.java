/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika._base.RequestHelper;
import de.bandika._base.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class AreaTag extends BaseTag {

  private String name = "";
  private String matchTypes = "";
  private boolean fixed = false;

  public void setName(String name) {
    this.name = name;
  }

  public void setMatchTypes(String matchTypes) {
    this.matchTypes = matchTypes;
  }

  public void setFixed(boolean fixed) {
    this.fixed = fixed;
  }

  public int doStartTag() throws JspException {
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
    try {
      boolean editMode = rdata.getParamBoolean("editMode");
      rdata.setParam("areaName", name);
      rdata.setParam("areaMatchTypes", matchTypes);
      if (editMode)
        context.include("/_jsp/page/editArea.jsp");
      else
        context.include("/_jsp/page/area.jsp");
      rdata.removeParam("areaName");
      rdata.removeParam("areaMatchTypes");
    } catch (Exception e) {
      rdata.setException(e);
      rdata.setCurrentJsp("/_jsp/error.jsp");
    }
    if (fixed)
      return EVAL_BODY_INCLUDE;
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    return 0;
  }

}
