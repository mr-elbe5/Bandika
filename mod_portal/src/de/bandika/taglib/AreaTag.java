/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.servlet.MasterResponse;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class AreaTag extends BaseTag {

    private String name = "";
    private String areaType = "";
    private boolean fixed = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public int doStartTag() throws JspException {
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
        try {
            boolean editMode = rdata.getBoolean("editMode");
            rdata.put("areaName", name);
            rdata.put("areaType", areaType);
            if (editMode)
                context.include("/WEB-INF/_jsp/page/editArea.jsp");
            else
                context.include("/WEB-INF/_jsp/page/area.jsp");
            rdata.remove("areaName");
            rdata.remove("areaType");
        } catch (Exception e) {
            rdata.setException(e);
            rdata.put(MasterResponse.KEY_JSP,"/WEB-INF/_jsp/error.jsp");
        }
        if (fixed)
            return EVAL_BODY_INCLUDE;
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return 0;
    }

}
