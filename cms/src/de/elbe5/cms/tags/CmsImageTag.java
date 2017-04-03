/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.ImageField;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class CmsImageTag extends CmsBaseTag {
    protected String className = "";
    protected ImageField field;

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String editTag = "<input type = \"hidden\" id = \"%sImgId\" name = \"%sImgId\" value = \"%s\"/>\n" +
            "    <input type = \"hidden\" id = \"%sAlt\" name = \"%sAlt\" value = \"%s\"/> \n" +
            "    <a href = \"#\" class = \"editField\" onclick = \"return openModalLayerDialog('%s', '/field.ajx?act=openSelectImage&fieldName=%s&className=%s');\"\"> \n" +
            "        <img class = \"editField %s\" id = \"%s\" src = \"%s\" alt = \"%s\" />" +
            "    </a>";
    private static final String runtimeTag = "<img class=\"%s\" src=\"/file.srv?act=show&fileId=%s\" alt=\"%s\" title=\"%s\" />";

    @Override
    protected void doEditTag(HttpServletRequest request) throws JspException {
        field = (ImageField) pdata.ensureField(name, ImageField.FIELDTYPE_IMAGE);
        try {
            JspWriter writer = getWriter();
            Locale locale = SessionHelper.getSessionLocale(request);
            writer.print(String.format(editTag,
                    field.getIdentifier(),
                    field.getIdentifier(),
                    field.getImgId(),
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getAltText()),
                    StringUtil.getHtml("_image",locale),
                    name,
                    className,
                    StringUtil.isNullOrEmtpy(className) ? "" : className,
                    field.getIdentifier(),
                    field.getImgId() > 0 ? "/file.srv?act=show&fileId="+field.getImgId() : "/_statics/img/dummy.gif",
                    StringUtil.toHtml(field.getAltText())));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(HttpServletRequest request) throws JspException {
        field = (ImageField) pdata.ensureField(name, ImageField.FIELDTYPE_IMAGE);
        try {
            JspWriter writer = getWriter();
            if (field.getImgId() > 0) {
                writer.print(String.format(runtimeTag,
                        className,
                        field.getImgId(),
                        field.getAltText(),
                        field.getAltText()));
            } else {
                writer.print("&nbsp;");
            }
        } catch (Exception ignored) {
        }
    }
}
