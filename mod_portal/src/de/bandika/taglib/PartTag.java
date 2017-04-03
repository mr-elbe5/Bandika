/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.data.StringCache;
import de.bandika.page.PageData;
import de.bandika.page.PagePartData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.SessionData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class PartTag extends BaseTag {

    private String template = "";
    private int ranking = 0;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    private static final String partEditTag = "" +
            "<div class=\"areatools\">" +
            "<button class=\"btn btn-micro btn-primary\" onclick=\"savePagePart(%s,'%s');\">%s</button>" +
            "<button class=\"btn btn-micro\" onclick=\"submitAction('cancelEditPagePart');\">%s</button>" +
            "</div>";

    private static final String editTag = "" +
            "<div class=\"areatools\">" +
            "<a class=\"btn btn-micro btn-tool\" href=\"/page.srv?act=editPagePart&pageId=%s&partId=%s&areaName=%s\" title=\"%s\">" +
            "<i class=\"icon-pencil icon-white\"></i>" +
            "</a>" +
            "</div>";

    public int doStartTag() throws JspException {
        PageData page;
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
        SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
        Locale locale=sdata.getLocale();
        boolean editMode = rdata.getInt("editMode", 0) > 0;
        if (editMode) {
            page = (PageData) sdata.get("pageData");
        } else {
            page = (PageData) rdata.get("pageData");
        }
        PagePartData data = page.ensureStaticPart(template, ranking);
        boolean partEditMode = page.getEditPagePart() == data;
        boolean otherEditMode = !partEditMode && page.getEditPagePart() != null;
        JspWriter writer = getWriter();
        rdata.put("pagePartData", data);
        try {
            if (editMode) {
                if (partEditMode) {
                    writer.print(String.format(partEditTag,
                            data.getId(),
                            PageData.STATIC_AREA_NAME,
                            StringCache.getHtml("webapp_ok",locale),
                            StringCache.getHtml("webapp_cancel",locale)));
                    rdata.put("partEditMode", "1");
                } else if (!otherEditMode) {
                    writer.print(String.format(editTag,
                            page.getId(),
                            data.getId(),
                            PageData.STATIC_AREA_NAME,
                            StringCache.getHtml("webapp_change",locale)));
                }
            }
            context.include("/WEB-INF/_jsp/_part/" + template + ".jsp");
            if (partEditMode) {
                rdata.remove("partEditMode");
            }
        } catch (Exception e) {
            throw new JspException(e);
        }
        rdata.remove("pagePartData");
        return SKIP_BODY;
    }

}
