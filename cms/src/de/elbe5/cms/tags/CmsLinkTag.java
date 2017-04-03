/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.FieldPartData;
import de.elbe5.cms.field.LinkField;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class CmsLinkTag extends CmsBaseTag {
    protected LinkField field;
    protected String className = "";

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String editStartTag = "<div>\n" +
            "    <input type=\"hidden\" id=\"%sLink\" name=\"%sLink\" value=\"%s\"/>\n" +
            "    <input type=\"hidden\" id=\"%sTarget\" name=\"%sTarget\" value=\"%s\"/>\n" +
            "    <a href=\"#\" class=\"editField %s\" onclick=\"openModalLayerDialog('%s', '/field.ajx?act=openSelectLink&fieldName=%s');\" id=\"%s\">";
    private static final String editEndTag = "</a>\n" + "</div>";
    private static final String runtimeStartTag = "<a href=\"%s\" %s %s>";
    private static final String runtimeEndTag = "</a>";

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        boolean editMode = RequestHelper.getBoolean(request, "partEditMode");
        pdata = (FieldPartData) request.getAttribute("pagePartData");
        if (editMode)
            doEditStartTag(request);
        else
            doRuntimeStartTag();
        return EVAL_BODY_INCLUDE;
    }

    protected void doEditStartTag(HttpServletRequest request) throws JspException {
        field = (LinkField) pdata.ensureField(name, LinkField.FIELDTYPE_LINK);
        try {
            JspWriter writer = getWriter();
            Locale locale = SessionHelper.getSessionLocale(request);
            writer.print(String.format(editStartTag,
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getLink()),
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getTarget()),
                    StringUtil.isNullOrEmtpy(className) ? "" : className,
                    StringUtil.getHtml("_link",locale),
                    field.getName(),
                    field.getIdentifier()));
        } catch (Exception ignored) {
        }
    }

    protected void doRuntimeStartTag() throws JspException {
        field = (LinkField) pdata.ensureField(name, LinkField.FIELDTYPE_LINK);
        if (!field.getLink().isEmpty()) {
            try {
                JspWriter writer = getWriter();
                writer.print(String.format(runtimeStartTag, field.getLink(), className.isEmpty() ? "" : "class=\"" + className + '\"', field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"'));
            } catch (Exception ignored) {
            }
        }
    }

    public int doEndTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        boolean editMode = RequestHelper.getInt(request, "partEditMode", 0) == 1;
        pdata = (FieldPartData) request.getAttribute("pagePartData");
        if (editMode)
            doEditEndTag();
        else
            doRuntimeEndTag(request);
        return 0;
    }

    protected void doEditEndTag() throws JspException {
        try {
            JspWriter writer = getWriter();
            writer.print(editEndTag);
        } catch (Exception ignored) {
        }
    }

    protected void doRuntimeEndTag(HttpServletRequest request) throws JspException {
        pdata = (FieldPartData) request.getAttribute("pagePartData");
        if (!field.getLink().isEmpty()) {
            try {
                JspWriter writer = getWriter();
                writer.print(runtimeEndTag);
            } catch (Exception ignored) {
            }
        }
    }
}
