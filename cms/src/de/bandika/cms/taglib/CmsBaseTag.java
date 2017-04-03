/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika._base.RequestHelper;
import de.bandika._base.RequestData;
import de.bandika.cms.CmsPartData;
import de.bandika.taglib.BaseTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class CmsBaseTag extends BaseTag {

  String name = "";
  CmsPartData pdata = null;

  public void setName(String name) {
    this.name = name;
  }

  public int doStartTag() throws JspException {
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) getContext().getRequest());
    boolean editMode = rdata.getParamInt("partEditMode", 0) == 1;
    pdata = (CmsPartData) rdata.getParam("pagePartData");
    if (editMode)
      doEditTag(rdata);
    else
      doRuntimeTag(rdata);
    return SKIP_BODY;
  }

  protected void doEditTag(RequestData rdata) throws JspException {
  }

  protected void doRuntimeTag(RequestData rdata) throws JspException {
  }

}