/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika._base.RequestHelper;
import de.bandika.application.StringCache;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;
import de.bandika.page.PageData;
import de.bandika.page.PagePartData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

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

  private static String partEditTag = "" +
    "<div class=\"areatools\">" +
    "<button class=\"btn btn-micro btn-primary\" onclick=\"savePagePart(%s,'%s');\">%s</i></button>" +
    "<button class=\"btn btn-micro\" onclick=\"submitMethod('cancelEditPagePart');\">%s</button>" +
    "</div>";

  private static final String editTag = "" +
    "<div class=\"areatools\">" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_page?method=editPagePart&id=%s&partId=%s&areaName=%s\" title=\"%s\">" +
    "<i class=\"icon-pencil icon-white\"></i>" +
    "</a>" +
    "</div>";

  public int doStartTag() throws JspException {
    PageData page;
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
    boolean editMode = rdata.getParamInt("editMode", 0) > 0;
    if (editMode) {
      SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
      page = (PageData) sdata.getParam("pageData");
    } else {
      page = (PageData) rdata.getParam("pageData");
    }
    PagePartData data = page.ensureStaticPart(template, ranking);
    boolean partEditMode = page.getEditPagePart() == data;
    boolean otherEditMode = !partEditMode && page.getEditPagePart() != null;
    JspWriter writer = getWriter();
    rdata.setParam("pagePartData", data);
    try {
      if (editMode) {
        if (partEditMode) {
          writer.print(String.format(partEditTag,
            data.getId(),
            PageData.STATIC_AREA_NAME,
            StringCache.getHtml("ok"),
            StringCache.getHtml("cancel")));
          rdata.setParam("partEditMode", "1");
        } else if (!otherEditMode) {
          writer.print(String.format(editTag,
            page.getId(),
            data.getId(),
            PageData.STATIC_AREA_NAME,
            StringCache.getHtml("change")));
        }
      }
      context.include("/_jsp/_part/" + template + ".jsp");
      if (partEditMode) {
        rdata.removeParam("partEditMode");
      }
    } catch (Exception e) {
      throw new JspException(e);
    }
    rdata.removeParam("pagePartData");
    return SKIP_BODY;
  }

}
