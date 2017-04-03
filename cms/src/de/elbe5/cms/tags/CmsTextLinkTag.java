/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.TextLinkField;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class CmsTextLinkTag extends CmsBaseTag {
    protected TextLinkField field;
    protected String className = "";

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String editTag = "<input type = \"hidden\" id = \"%sText\" name = \"%sText\" value = \"%s\"/> \n" +
            "    <input type = \"hidden\" id = \"%sLink\" name = \"%sLink\" value = \"%s\"/> \n" +
            "    <input type = \"hidden\" id = \"%sTarget\" name = \"%sTarget\" value = \"%s\"/> \n" +
            "    <a href = \"#\" class = \"editField %s\" onclick = \"return openModalLayerDialog('%s', '/field.ajx?act=openSelectTextLink&fieldName=%s');\"\">%s</a>";
    private static final String runtimeTag = "<a href=\"%s\" %s %s>%s</a>";

    @Override
    protected void doEditTag(HttpServletRequest request) throws JspException {
        field = (TextLinkField) pdata.ensureField(name, TextLinkField.FIELDTYPE_TEXTLINK);
        try {
            JspWriter writer = getWriter();
            Locale locale = SessionHelper.getSessionLocale(request);
            writer.print(String.format(editTag,
                    field.getIdentifier(),
                    field.getIdentifier(),
                    field.getText(),
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getLink()),
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getTarget()),
                    StringUtil.isNullOrEmtpy(className) ? "" : className,
                    StringUtil.getHtml("_textLink", locale),
                    name,
                    StringUtil.isNullOrEmtpy(field.getText()) ?
                        StringUtil.getHtml("_dummyLinkText", locale) :
                        StringUtil.toHtml(field.getText())));

        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(HttpServletRequest request) throws JspException {
        field = (TextLinkField) pdata.ensureField(name, TextLinkField.FIELDTYPE_TEXTLINK);
        try {
            JspWriter writer = getWriter();
            if (!field.getLink().isEmpty()) {
                writer.print(String.format(runtimeTag, field.getLink(), className.isEmpty() ? "" : "class=\"" + className + '\"', field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"', StringUtil.toHtml(field.getText())));
            } else {
                writer.print(StringUtil.toHtml(field.getText()));
            }
        } catch (Exception ignored) {
        }
    }
}
