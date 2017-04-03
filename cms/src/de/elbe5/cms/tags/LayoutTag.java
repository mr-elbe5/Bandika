/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.webserver.servlet.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class LayoutTag extends BaseTag {
    protected boolean editMode = false;
    protected static final String editStartTag = "<div class=\"editArea\">";
    protected static final String editEndTag = "</div><script>$('.editArea').initEditArea();</script>";
    protected static final String runtimeStartTag = "<div class=\"viewArea\">";
    protected static final String runtimeEndTag = "</div>";

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        editMode = RequestHelper.getBoolean(request, "editMode");
        JspWriter writer = getWriter();
        try {
            if (editMode) {
                writer.println(editStartTag);
            } else {
                writer.println(runtimeStartTag);
            }
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        editMode = RequestHelper.getBoolean(request, "editMode");
        JspWriter writer = getWriter();
        try {
            if (editMode)
                writer.println(editEndTag);
            else
                writer.println(runtimeEndTag);
        } catch (Exception e) {
            throw new JspException(e);
        }
        return 0;
    }
}
