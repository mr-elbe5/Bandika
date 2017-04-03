/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.data.StringFormat;
import de.bandika.data.StringCache;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.SessionData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class FileUploadTag extends BaseTag {

    private String name = "";

    public void setName(String name) {
        this.name = name;
    }

    static String tag = "<div class=\"fileupload fileupload-new\" data-provides=\"fileupload\">" +
            "<div class=\"uneditable-input span3\">" +
            "<i class=\"icon-file fileupload-exists\"></i>" +
            "<span class=\"fileupload-preview\"></span>" +
            "</div>" +
            "<span class=\"btn btn-file\">" +
            "<span class=\"fileupload-new\">%s</span>" +
            "<span class=\"fileupload-exists\">%s</span>" +
            "<input type=\"file\" id=\"%s\" name=\"%s\" />" +
            "</span>" +
            "<a href=\"#\" class=\"btn fileupload-exists\" data-dismiss=\"fileupload\">%s</a>" +
            "</div>";

    public int doStartTag() throws JspException {
        SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
        Locale locale=sdata.getLocale();
        JspWriter writer = getWriter();
        try {
            writer.print(String.format(tag,
                    StringCache.getHtml("portal_selectFile",locale),
                    StringCache.getHtml("webapp_change",locale),
                    StringFormat.toHtml(name),
                    StringFormat.toHtml(name),
                    StringCache.getHtml("webapp_remove",locale)));
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return 0;
    }

}