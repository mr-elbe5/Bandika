/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PagePartData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class PartInclude {

    protected static final String partEditStartTag = "<div class = \"editPagePart\">";
    protected static final String partStartTag = "<div class = \"viewPagePart\">";
    protected static final String partContextStartTag = "<div class = \"viewPagePart contextSource\">";
    protected static final String partEndTag = "</div>";

    protected static final String formStartTag = "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">\n" +
            "            <input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>" +
            "            <input type = \"hidden\" name = \"pageId\" value = \"%s\"/>" +
            "            <input type = \"hidden\" name = \"areaName\" value = \"%s\"/>" +
            "            <input type = \"hidden\" name = \"partId\" value = \"%s\"/>";
    protected static final String formButtonsTag = "<div class = \"buttonset area\">\n" +
            "            <button type = \"submit\" class = \"primary icn iok\" onclick = \"evaluateEditFields();return true;\">%s</button>\n" +
            "            <button class=\"icn icancel\" onclick = \"linkTo('/pagepart.srv?act=cancelEditPagePart&pageId=%s');\">%s</button>\n" +
            "        </div>";
    protected static final String formEndTag = "</form>";

    protected static final String contextAddAboveTag="<div class=\"icn inew\" onclick = \"return openModalLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&areaName=%s&areaType=%s&partId=%s');\">%s</div>";
    protected static final String contextAddBelowTag="<div class=\"icn inew\" onclick = \"return openModalLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&areaName=%s&areaType=%s&partId=%s&below=true');\">%s</div>";
    protected static final String contextEditTag="<div class=\"icn iedit\" onclick = \"return linkTo('/pagepart.srv?act=editPagePart&pageId=%s&areaName=%s&partId=%s')\">%s</div>";
    protected static final String contextShareTag="<div class=\"icn ishare\" onclick = \"return openModalLayerDialog('%s', '/pagepart.srv?act=openSharePagePart&pageId=%s&areaName=%s&partId=%s');\">%s</div>";
    protected static final String contextUpTag="<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&areaName=%s&partId=%s&dir=-1')\">%s</div>";
    protected static final String contextDownTag="<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&areaName=%s&partId=%s&dir=1')\">%s</div>";
    protected static final String contextDeleteTag="<div class=\"icn idelete\" onclick = \"return linkTo('/pagepart.srv?act=deletePagePart&pageId=%s&areaName=%s&partId=%s')\">%s</div>";

    public static void writeEditPartStart(PagePartData pdata, PagePartData editPagePart, String areaName, int pageId, JspWriter writer, Locale locale, HttpServletRequest request) throws Exception{
        if (editPagePart == null){
            writer.println(partContextStartTag);
        }
        else if (pdata == editPagePart){
            writer.println(partEditStartTag);
            writer.println(String.format(formStartTag,
                    pageId,
                    areaName,
                    pdata.getId()));
            writer.println(String.format(formButtonsTag,
                    StringUtil.getHtml("_ok",locale),
                    pageId,
                    StringUtil.getHtml("_cancel",locale)));
            request.setAttribute("partEditMode", "true");
        }
        else{
            writer.println(partStartTag);
        }
    }

    public static void writeEditPartEnd(PagePartData pdata, PagePartData editPagePart, String areaType, String areaName, int pageId, JspWriter writer, Locale locale, HttpServletRequest request) throws Exception{
        writer.println(partEndTag);
        if (editPagePart == null){
            writer.println("<div class = \"contextMenu\">");
            writer.println(String.format(contextAddAboveTag,
                    StringUtil.getHtml("_addPart", locale),
                    pageId,
                    areaName,
                    areaType,
                    pdata.getId(),
                    StringUtil.getHtml("_newAbove", locale)));
            writer.println(String.format(contextAddBelowTag,
                    StringUtil.getHtml("_addPart", locale),
                    pageId,
                    areaName,
                    areaType,
                    pdata.getId(),
                    StringUtil.getHtml("_newBelow", locale)));
            writer.println(String.format(contextEditTag,
                    pageId,
                    areaName,
                    pdata.getId(),
                    StringUtil.getHtml("_edit", locale)));
            writer.println(String.format(contextShareTag,
                    StringUtil.getHtml("_share", locale),
                    pageId,
                    areaName,
                    pdata.getId(),
                    StringUtil.getHtml("_share", locale)));
            writer.println(String.format(contextUpTag,
                    pageId,
                    areaName,
                    pdata.getId(),
                    StringUtil.getHtml("_up", locale)));
            writer.println(String.format(contextDownTag,
                    pageId,
                    areaName,
                    pdata.getId(),
                    StringUtil.getHtml("_down", locale)));
            writer.println(String.format(contextDeleteTag,
                    pageId,
                    areaName,
                    pdata.getId(),
                    StringUtil.getHtml("_delete", locale)));
            writer.println("</div>");
        }
        else if (pdata == editPagePart){
            writer.println(formEndTag);
            request.removeAttribute("partEditMode");
        }
    }

    public static void writeEditStaticPartStart(PagePartData pdata, PagePartData editPagePart, String areaName, int pageId, JspWriter writer, Locale locale, HttpServletRequest request) throws Exception{
        if (editPagePart == null){
            writer.println(partContextStartTag);
        }
        else if (pdata == editPagePart){
            writer.println(partEditStartTag);
            writer.println(String.format(formStartTag,
                    pageId,
                    areaName,
                    pdata.getId()));
            writer.println(String.format(formButtonsTag,
                    StringUtil.getHtml("_ok",locale),
                    pageId,
                    StringUtil.getHtml("_cancel",locale)));
            request.setAttribute("partEditMode", "true");
        }
        else{
            writer.println(partStartTag);
        }
    }

    public static void writeEditStaticPartEnd(PagePartData pdata, PagePartData editPagePart, int pageId, JspWriter writer, Locale locale, HttpServletRequest request) throws Exception{
        writer.println(partEndTag);
        if (editPagePart == null){
            writer.println("<div class = \"contextMenu\">");
            writer.println(String.format(contextEditTag,
                    pageId,
                    PageData.STATIC_AREA_NAME,
                    pdata.getId(),
                    StringUtil.getHtml("_edit", locale)));
            writer.println("</div>");
        }
        else if (pdata == editPagePart){
            writer.println(formEndTag);
            request.removeAttribute("partEditMode");
        }
    }
}
