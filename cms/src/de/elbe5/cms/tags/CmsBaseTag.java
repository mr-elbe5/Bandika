/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.cms.field.FieldPartData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class CmsBaseTag extends BaseTag {
    protected String name = "";
    protected FieldPartData pdata = null;

    public void setName(String name) {
        this.name = name;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        boolean editMode = RequestHelper.getBoolean(request, "partEditMode", false);
        pdata = (FieldPartData) request.getAttribute("pagePartData");
        if (editMode)
            doEditTag(request);
        else
            doRuntimeTag(request);
        return SKIP_BODY;
    }

    protected void doEditTag(HttpServletRequest request) throws JspException {
    }

    protected void doRuntimeTag(HttpServletRequest request) throws JspException {
    }
}
