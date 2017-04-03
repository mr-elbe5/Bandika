/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika.cms.CmsPartData;
import de.bandika.cms.LinkField;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class LinkTag extends CmsBaseTag {

    protected LinkField field;
    protected String className = "";
    protected String activeType = "page";
    protected String availableTypes = "page,document,image";

    public void setClassName(String className) {
        this.className = className;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public void setAvailableTypes(String availableTypes) {
        this.availableTypes = availableTypes;
    }

    private static final String runtimeStartTag = "<a href=\"%s\" %s %s>";
    private static final String runtimeEndTag = "</a>";

    public int doStartTag() throws JspException {
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) getContext().getRequest());
        boolean editMode = rdata.getInt("partEditMode", 0) == 1;
        pdata = (CmsPartData) rdata.get("pagePartData");
        if (editMode)
            doEditStartTag(rdata);
        else
            doRuntimeStartTag();
        return EVAL_BODY_INCLUDE;
    }

    protected void doEditStartTag(RequestData rdata) throws JspException {
        field = (LinkField) pdata.ensureField(name, LinkField.FIELDTYPE_LINK);
        try {
            rdata.put("fieldName", name);
            rdata.put("className", className);
            rdata.put("activeType", activeType);
            rdata.put("availableTypes", availableTypes);
            context.include("/WEB-INF/_jsp/cms/selectLinkStart.jsp");
        } catch (Exception ignored) {
        }
    }

    protected void doRuntimeStartTag() throws JspException {
        field = (LinkField) pdata.ensureField(name, LinkField.FIELDTYPE_LINK);
        if (!field.getLink().isEmpty()) {
            try {
                JspWriter writer = getWriter();
                writer.print(String.format(runtimeStartTag,
                        field.getLink(),
                        className.isEmpty() ? "" : "class=\"" + className + '\"',
                        field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"'));
            } catch (Exception ignored) {
            }
        }
    }

    public int doEndTag() throws JspException {
        RequestData rdata = RequestHelper.getRequestData((HttpServletRequest) getContext().getRequest());
        boolean editMode = rdata.getInt("partEditMode", 0) == 1;
        pdata = (CmsPartData) rdata.get("pagePartData");
        if (editMode)
            doEditEndTag(rdata);
        else
            doRuntimeEndTag(rdata);
        return 0;
    }

    protected void doEditEndTag(RequestData rdata) throws JspException {
        field = (LinkField) pdata.getField(name);
        try {
            context.include("/WEB-INF/_jsp/cms/selectLinkEnd.jsp");
            rdata.remove("fieldName");
            rdata.remove("className");
            rdata.remove("activeType");
            rdata.remove("availableTypes");
        } catch (Exception ignored) {
        }
    }

    protected void doRuntimeEndTag(RequestData rdata) throws JspException {
        pdata = (CmsPartData) rdata.get("pagePartData");
        if (!field.getLink().isEmpty()) {
            try {
                JspWriter writer = getWriter();
                writer.print(runtimeEndTag);
            } catch (Exception ignored) {
            }
        }
    }

}
