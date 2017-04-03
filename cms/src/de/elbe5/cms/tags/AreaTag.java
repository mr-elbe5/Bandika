/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.AreaData;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PagePartData;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class AreaTag extends BaseTag {
    private String areaName = "";
    private String areaType = "";

    public void setAreaName(String name) {
        this.areaName = name;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    protected static final String areaStartTag = "<div class = \"area\">";
    protected static final String areaContextStartTag = "<div class = \"area contextSource\">";
    protected static final String areaEndTag = "</div>";
    protected static final String areaContextTag = "<div class = \"contextMenu\">\n" +
            "    <div class=\"icn inew\" onclick = \"return openModalLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&areaName=%s&areaType=%s&partId=-1');\">%s\n" +
            "    </div>\n" +
            "</div>";

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        JspWriter writer = getWriter();
        Locale locale = SessionHelper.getSessionLocale(request);
        boolean editMode = RequestHelper.getBoolean(request, "editMode");
        PageData page;
        if (editMode) {
            page = (PageData) SessionHelper.getSessionObject(request, "pageData");
        } else {
            page = (PageData) request.getAttribute("pageData");
        }
        if (page!=null){
            AreaData area = page.getArea(areaName);
            if (area==null){
                page.ensureArea(areaName);
                area = page.getArea(areaName);
            }
            if (area!=null){
                PagePartData editPagePart = editMode ? page.getEditPagePart() : null;
                boolean hasParts = area.getParts().size()>0;
                try {
                    writeAreaStart(editMode,!hasParts,writer);
                    request.setAttribute("areaName", areaName);
                    request.setAttribute("areaType", areaType);
                    for (PagePartData pdata : area.getParts()) {
                        if (editMode)
                            PartInclude.writeEditPartStart(pdata, editPagePart, areaName, page.getId(), writer, locale, request);
                        request.setAttribute("pagePartData", pdata);
                        String url = pdata.getPartTemplateUrl();
                        try {
                            context.include(url);
                        }catch (Exception e) {
                            writer.println("<div>"+StringUtil.getHtml("_templateNotFound", locale)+":"+url+"</div>");
                        }
                        request.removeAttribute("pagePartData");
                        if (editMode)
                            PartInclude.writeEditPartEnd(pdata, editPagePart, areaType, areaName, page.getId(), writer, locale, request);
                    }
                    request.removeAttribute("areaName");
                    request.removeAttribute("areaType");
                    writeAreaEnd(editMode, !hasParts, page.getId(), writer, locale);
                } catch (Exception e) {
                    RequestHelper.setException(request, e);
                    request.setAttribute(ResponseHelper.KEY_JSP, "/WEB-INF/_jsp/error.inc.jsp");
                }
            }
        }
        return SKIP_BODY;
    }

    protected void writeAreaStart(boolean editMode, boolean showContext, JspWriter writer) throws Exception{
        if (editMode){
            writer.println(showContext ? areaContextStartTag : areaStartTag);
        }
    }

    protected void writeAreaEnd(boolean editMode, boolean showContext, int pageId, JspWriter writer, Locale locale) throws Exception{
        if (editMode){
            writer.println(areaEndTag);
            if (showContext){
                String newString= StringUtil.getHtml("_new", locale);
                writer.println(String.format(areaContextTag,
                        StringUtil.getHtml("_addPart", locale),
                        pageId,
                        areaName,
                        areaType,
                        newString));
            }
        }
    }

    public int doEndTag() throws JspException {
        return 0;
    }
}
