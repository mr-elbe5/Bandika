/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageData;
import de.bandika.cms.templatecontrol.TemplateControl;
import de.bandika.cms.templatecontrol.TemplateControls;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class TemplateControlTag extends BaseTag {
    private String name = "";
    private String content = "";
    private String pageKey = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    @Override
    public int doStartTag() {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        PageData pageData = null;
        try {
            if (!pageKey.isEmpty())
                pageData = (PageData) request.getAttribute(pageKey);
            TemplateControl control = TemplateControls.getControl(name);
            if (control != null) {
                control.appendHtml(context, getWriter(), request, null, content, pageData);
            }
        } catch (Exception e) {
            Log.error("could not write control tag", e);
        }
        return SKIP_BODY;
    }

}
