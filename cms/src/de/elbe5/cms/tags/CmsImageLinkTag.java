/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.ImageLinkField;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class CmsImageLinkTag extends CmsBaseTag {
    protected ImageLinkField field;
    protected String className = "";

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String editTag = "<input type = \"hidden\" id = \"%sImgId\" name = \"%sImgId\" value = \"%s\"/>\n" +
            "    <input type = \"hidden\" id = \"%sAlt\" name = \"%sAlt\" value = \"%s\"/> \n" +
            "    <input type = \"hidden\" id = \"%sLink\" name = \"%sLink\" value = \"%s\"/> \n" +
            "    <input type = \"hidden\" id = \"%sTarget\" name = \"%sTarget\" value = \"%s\"/> \n" +
            "    <a href = \"#\" class = \"editField\" onclick = \"return openModalLayerDialog('%s', '/field.ajx?act=openSelectImageLink&fieldName=%s&className=%s');\"\"> \n" +
            "        <img class = \"editField %s\" id = \"%s\" src = \"%s\" alt = \"%s\" />" +
            "    </a>";
    private static final String runtimeTag = "<a href=\"%s\" %s %s>%s</a>";
    private static final String imageRuntimeTag = "<img src=\"/file.srv?act=show&fileId=%s\" alt=\"%s\" title=\"%s\" />";

    @Override
    protected void doEditTag(HttpServletRequest request) throws JspException {
        field = (ImageLinkField) pdata.ensureField(name, ImageLinkField.FIELDTYPE_IMAGELINK);
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
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getLink()),
                    field.getIdentifier(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getTarget()),
                    StringUtil.getHtml("_imageLink",locale),
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
        field = (ImageLinkField) pdata.ensureField(name, ImageLinkField.FIELDTYPE_IMAGELINK);
        try {
            JspWriter writer = getWriter();
            if (!field.getLink().isEmpty()) {
                writer.print(String.format(runtimeTag, field.getLink(), className.isEmpty() ? "" : "class=\"" + className + '\"', field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"', field.getImgId() > 0 ? String.format(imageRuntimeTag, field.getImgId(), field.getAltText(), field.getAltText()) : "&nbsp;"));
            } else {
                writer.print(field.getImgId() > 0 ? String.format(imageRuntimeTag, field.getImgId(), field.getAltText(), field.getAltText()) : "&nbsp;");
            }
        } catch (Exception ignored) {
        }
    }
}
