/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;

public class ContactTag extends BaseTag {

    private String cssClass = "";

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            Writer writer = getWriter();
            writer.write(StringUtil.format("<div class=\"{1}\">", StringUtil.toHtml(cssClass)));
            getContext().include("/WEB-INF/_jsp/page/contact.inc.jsp");
            writer.write("</div>");
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}
