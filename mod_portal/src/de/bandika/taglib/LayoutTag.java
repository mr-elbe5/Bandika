/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.page.PageData;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.SessionData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class LayoutTag extends BaseTag {

    private boolean editMode = false;

    private static final String runtimeStartTag = "<div class=\"viewArea\">";
    private static final String runtimeEndTag = "</div>";

    public int doStartTag() throws JspException {
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
        editMode = rdata.getInt("editMode", 0) > 0;
        JspWriter writer = getWriter();
        try {
            if (editMode) {
                context.include("/WEB-INF/_jsp/page/editLayoutStart.jsp");
            } else {
                writer.println(runtimeStartTag);
            }
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
        SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
        PageData data = (PageData) rdata.get("pageData");
        JspWriter writer = getWriter();
        try {
            if (editMode) {
                context.include("/WEB-INF/_jsp/page/editLayoutEnd.jsp");
            } else {
                writer.println(runtimeEndTag);
            }
            if (!editMode && (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getId(), PageRightsData.RIGHT_CREATE))) {
                context.include("/WEB-INF/_jsp/page/createPageLayer.inc.jsp");
            }
        } catch (Exception e) {
            throw new JspException(e);
        }
        return 0;
    }
}
