/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class BaseTag implements Tag {
    protected Tag parent = null;
    protected PageContext context = null;

    public void setPageContext(PageContext pageContext) {
        context = pageContext;
    }

    public PageContext getContext() {
        return context;
    }

    public JspWriter getWriter() {
        return context.getOut();
    }

    public void setParent(Tag tag) {
        parent = tag;
    }

    public Tag getParent() {
        return parent;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        return 0;
    }

    public void release() {
    }

}
