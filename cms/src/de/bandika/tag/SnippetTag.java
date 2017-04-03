/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.tag;

import de.bandika.base.log.Log;
import de.bandika.template.TemplateCache;
import de.bandika.template.TemplateData;
import de.bandika.template.TemplateType;

import javax.servlet.jsp.JspException;

public class SnippetTag extends BaseTag {
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int doStartTag() throws JspException {
        try {
            TemplateData snippet = TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, name);
            getWriter().print(snippet == null ? "" : snippet.getCode());
        } catch (Exception e) {
            Log.error("could not write snippet tag", e);
        }
        return SKIP_BODY;
    }

}
