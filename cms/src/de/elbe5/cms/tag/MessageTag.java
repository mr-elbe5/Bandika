/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.cache.StringCache;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.request.RequestData;

import javax.servlet.jsp.JspException;
import java.io.Writer;
import java.util.Locale;

public class MessageTag extends BaseTag {

    String controlHtml = "" +
            "<div class=\"alert alert-{1} alert-dismissible fade show\" role=\"alert\">\n" +
            "  <strong>{2}:</strong>&nbsp;{3}\n" +
            "  <button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">\n" +
            "    <span aria-hidden=\"true\">&times;</span>\n" +
            "  </button>\n" +
            "</div>";

    @Override
    public int doStartTag() throws JspException {
        try {
            RequestData rdata = getRequestData();
            if (rdata.hasMessage()) {
                String msg = rdata.getString(Statics.KEY_MESSAGE);
                String msgType = rdata.getString(Statics.KEY_MESSAGETYPE);
                Locale locale = rdata.getSessionLocale();
                Writer writer = getWriter();
                writer.write(StringUtil.format(controlHtml, msgType, StringCache.getHtml(Statics.getTypeKey(msgType), locale), StringUtil.toHtml(msg)));
            }
        } catch (Exception e) {
            Log.error("error writing message tag", e);
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}
