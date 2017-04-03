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
import de.bandika.page.PagePartData;
import de.bandika.reusable.ReusablePartContainer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class ReusablePartTag extends BaseTag {

  private String matchTypes = "";

  public void setMatchTypes(String matchTypes) {
    this.matchTypes = matchTypes;
  }

  private static final String startTag = "" +
    "<div class=\"outerEditArea\">" +
    "<form class=\"form-horizontal\" action=\"/_reusable\" method=\"post\" name=\"form\" accept-charset=\"UTF-8\">" +
    "<input type=\"hidden\" name=\"method\" value=\"saveReusablePart\"/>" +
    "<input type=\"hidden\" name=\"partId\" value=\"%s\"/>" +
    "<div>%s&nbsp;-&nbsp;%s</div>" +
    "<div class=\"btn-toolbar\">" +
    "<button class=\"btn btn-primary\" onclick=\"return submitMethod('saveReusablePart');\" >%s</button>" +
    "<button class=\"btn\" onclick=\"linkTo('/_reusable?method=reopenEditReusableParts');\" >%s</button>" +
    "</div>" +
    "<div class=\"editArea\">";

  private static final String partEditModeTag = "" +
    "<div class=\"areatools\">" +
    "<button class=\"btn btn-micro btn-primary\" onclick=\"submitMethod('savePagePart');\">%s</i></button>" +
    "<button class=\"btn btn-micro\" onclick=\"submitMethod('cancelEditPagePart');\">%s</button>" +
    "</div>";

  private static final String editModeTag = "" +
    "<div class=\"areaTools\">" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_reusable?method=editPagePart&partId=%s\">" +
    "<i class=\"icon-pencil\"></i>" +
    "</a>" +
    "</div>";

  private static final String endTag = "" +
    "</div>" +
    "</form>" +
    "</div>";

  public int doStartTag() throws JspException {
    ReusablePartContainer container;
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
    container = (ReusablePartContainer) sdata.getParam("partContainer");
    PagePartData data = container.getPagePart();
    JspWriter writer = getWriter();
    rdata.setParam("pagePartData", data);
    rdata.setParam("areaMatchTypes", matchTypes);
    try {
      writer.println(String.format(startTag,
        data.getId(),
        StringCache.getHtml("reusablePart"),
        data.getName(),
        StringCache.getHtml("save"),
        StringCache.getHtml("cancel")));
      if (data == container.getEditPagePart()) {
        writer.println(String.format(partEditModeTag,
          StringCache.getHtml("ok"),
          StringCache.getHtml("cancel")));
        rdata.setParam("partEditMode", "1");
      } else {
        writer.println(String.format(editModeTag,
          data.getId()));
      }
      context.include("/_jsp/_part/" + data.getPartTemplate() + ".jsp");
      writer.println(endTag);
      rdata.removeParam("partEditMode");
    } catch (Exception e) {
      throw new JspException(e);
    }
    rdata.removeParam("areaMatchTypes");
    rdata.removeParam("pagePartData");
    return SKIP_BODY;
  }

}