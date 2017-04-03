/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika._base.*;
import de.bandika.application.StringCache;
import de.bandika.page.PageRightsProvider;
import de.bandika.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class LayoutTag extends BaseTag {

  private int id = 0;
  private boolean editMode = false;

  public void setId(int id) {
    this.id = id;
  }

  private static final String editStartTag1 = "<div class=\"outerEditArea\">" +
    "<form class=\"form-horizontal\" action=\"/_page\" method=\"post\" name=\"form\" accept-charset=\"UTF-8\">" +
    "<input type=\"hidden\" name=\"method\" value=\"savePageFromContent\"/>" +
    "<input type=\"hidden\" name=\"id\" value=\"%s\"/>" +
    "<input type=\"hidden\" name=\"areaName\" value=\"\"/>" +
    "<input type=\"hidden\" name=\"partId\" value=\"0\"/>" +
    "<input type=\"hidden\" name=\"template\" value=\"\"/>" +
    "<input type=\"hidden\" name=\"partMethod\" value=\"\"/>";
  private static final String controlButtonsTag1 = "<div class=\"btn-toolbar\">\n" +
    "<button class=\"btn btn-primary\" onclick=\"return submitMethod('savePageFromContent');\" %s>%s</button>";
  private static final String controlButtonsTag2 = "<button class=\"btn btn-primary\" onclick=\"return submitMethod('saveAndPublishPageFromContent');\" %s>%s</button>";
  private static final String controlButtonsTag3 = "<button class=\"btn btn-primary\" onclick=\"return submitMethod('openEditPageSettingsFromContent');\" %s>%s</button>\n" +
    "<button class=\"btn\" onclick=\"return linkTo('/_page?method=stopEditing&id=%s');\" >%s</button>\n" +
    "</div>";
  private static final String editStartTag2 = "<div class=\"editArea\">";
  private static final String runtimeStartTag = "<div class=\"viewArea\">";
  private static final String editEndTag = "</div></form></div>";
  private static final String runtimeEndTag = "</div>";

  public int doStartTag() throws JspException {
    RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) context.getRequest());
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
    PageData data = (PageData) rdata.getParam("pageData");
    if (data == null) {
      data = (PageData) sdata.getParam("pageData");
    }
    editMode = rdata.getParamInt("editMode", 0) > 0;
    boolean editingPart = data.getEditPagePart() != null;
    JspWriter writer = getWriter();
    try {
      if (editMode) {
        writer.print(String.format(editStartTag1,
          id));
        writer.print(String.format(controlButtonsTag1,
          editingPart ? "disabled" : "",
          StringCache.getHtml("save")));
        if (sdata.isApprover() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getId(), IRights.ROLE_APPROVER)) {
          writer.print(String.format(controlButtonsTag2,
            editingPart ? "disabled" : "",
            StringCache.getHtml("publish")));
        }
        writer.print(String.format(controlButtonsTag3,
          editingPart ? "disabled" : "",
          StringCache.getHtml("pageSettings"),
          data.getId(),
          StringCache.getHtml("cancel")));
        writer.println(editStartTag2);
      } else {
        writer.println(runtimeStartTag);
      }
    } catch (Exception e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) context.getRequest());
    JspWriter writer = getWriter();
    try {
      if (editMode) {
        writer.println(editEndTag);
      } else {
        writer.println(runtimeEndTag);
      }
      if (!editMode && sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.RIGHT_CREATE)) {
        context.include("/_jsp/page/createPageLayer.inc.jsp");
      }
    } catch (Exception e) {
      throw new JspException(e);
    }
    return 0;
  }

}
