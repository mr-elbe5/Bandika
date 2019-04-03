/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.servlet.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class SearchBoxTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            Locale locale= rdata.getSessionLocale();
            writer.write("<form action=\"/search/search\" method=\"post\" id=\"searchboxform\" name=\"searchboxform\" accept-charset=\"UTF-8\">\n" +
                    "  <div class=\"input-group\">\n" +
                    "    <input id=\"searchPattern\" name=\"searchPattern\" type=\"text\" class=\"form-control\" placeholder=\"");
            writer.write(Strings._search.html(locale));
            writer.write("\" maxlength=\"60\"/>\n" +
                    "    <div class=\"input-group-append\">\n" +
                    "      <button class=\"btn btn-outline-secondary fa fa-search\" type=\"submit\"></button>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</form>");


        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

