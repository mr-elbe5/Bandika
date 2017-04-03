/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.template.TemplateControl;
import de.elbe5.template.TemplateControls;

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

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        PageData pageData=null;
        try {
            if (!pageKey.isEmpty())
                pageData = (PageData) request.getAttribute(pageKey);
            TemplateControl control = TemplateControls.getControl(name);
            if (control != null) {
                StringBuilder sb = new StringBuilder();
                control.appendHtml(sb, null, content, pageData, request);
                getWriter().print(sb.toString());
            }
        }
        catch (Exception e){
            Log.error("could not write control tag", e);
        }
        return SKIP_BODY;
    }

}
