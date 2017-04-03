/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika.cms.TextLinkField;
import de.bandika.data.StringFormat;
import de.bandika.servlet.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextLinkTag extends CmsBaseTag {

    protected TextLinkField field;
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

    private static final String runtimeTag = "<a href=\"%s\" %s %s>%s</a>";

    @Override
    protected void doEditTag(RequestData rdata) throws JspException {
        field = (TextLinkField) pdata.ensureField(name, TextLinkField.FIELDTYPE_TEXTLINK);
        try {
            rdata.put("fieldName", name);
            rdata.put("className", className);
            rdata.put("activeType", activeType);
            rdata.put("availableTypes", availableTypes);
            context.include("/WEB-INF/_jsp/cms/selectTextLink.jsp");
            rdata.remove("fieldName");
            rdata.remove("className");
            rdata.remove("activeType");
            rdata.remove("availableTypes");
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(RequestData rdata) throws JspException {
        field = (TextLinkField) pdata.ensureField(name, TextLinkField.FIELDTYPE_TEXTLINK);
        try {
            JspWriter writer = getWriter();
            if (!field.getLink().isEmpty()) {
                writer.print(String.format(runtimeTag,
                        field.getLink(),
                        className.isEmpty() ? "" : "class=\"" + className + '\"',
                        field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"',
                        StringFormat.toHtml(field.getText())));
            } else {
                writer.print(StringFormat.toHtml(field.getText()));
            }
        } catch (Exception ignored) {
        }
    }

}
