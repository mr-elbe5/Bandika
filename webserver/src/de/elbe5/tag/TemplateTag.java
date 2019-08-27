/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;

public abstract class TemplateTag extends BaseTag {

    protected String name = "";

    public void setName(String name) {
        this.name = name;
    }

    protected abstract String getType();

    @Override
    public int doStartTag() {
        try {
            JspWriter writer = getContext().getOut();
            String url = "/WEB-INF/_jsp/_templates/" + getType() + "/" + name + ".jsp";
            try {
                getContext().include(url);
            } catch (ServletException e) {
                Log.error("could not include template jsp:" + url, e);
                writer.write("<div>Template JSP missing</div>");
            }

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

