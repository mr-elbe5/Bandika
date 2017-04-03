/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.TextAreaField;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class CmsTextAreaTag extends CmsBaseTag {
    protected String height = "";
    protected TextAreaField field;

    public void setHeight(String height) {
        this.height = height;
    }

    private static final String editTag = "<textarea class=\"editField\" name=\"%s\" rows=\"5\" %s >%s</textarea>";
    private static final String styleAttr = "style=\"height:%s\"";

    @Override
    protected void doEditTag(HttpServletRequest request) throws JspException {
        field = (TextAreaField) pdata.ensureField(name, TextAreaField.FIELDTYPE_TEXTAREA);
        try {
            JspWriter writer = getWriter();
            writer.print(String.format(editTag, field.getIdentifier(), height.isEmpty() ? String.format(styleAttr, height) : "", StringUtil.toHtmlInput(field.getText())));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(HttpServletRequest request) throws JspException {
        field = (TextAreaField) pdata.ensureField(name, TextAreaField.FIELDTYPE_TEXTAREA);
        try {
            JspWriter writer = getWriter();
            if (field.getText().length() == 0) writer.print("&nbsp;");
            else writer.print(StringUtil.toHtml(field.getText()).replace("\n", "<br/>\n"));
        } catch (Exception ignored) {
        }
    }
}
