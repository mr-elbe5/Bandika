/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika.cms.ImageLinkField;
import de.bandika.servlet.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ImageLinkTag extends CmsBaseTag {

    protected ImageLinkField field;

    protected String activeType = "page";
    protected String availableTypes = "page,document,image";
    protected String className = "";


    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public void setAvailableTypes(String availableTypes) {
        this.availableTypes = availableTypes;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String runtimeTag = "<a href=\"%s\" %s %s>%s</a>";
    private static final String imageRuntimeTag = "<img src=\"/image.srv?act=show&fid=%s\" alt=\"%s\" title=\"%s\" style=\"width:%s; height:%s\" />";

    @Override
    protected void doEditTag(RequestData rdata) throws JspException {
        field = (ImageLinkField) pdata.ensureField(name, ImageLinkField.FIELDTYPE_IMAGELINK);
        try {
            rdata.put("fieldName", name);
            rdata.put("className", className);
            rdata.put("activeType", activeType);
            rdata.put("availableTypes", availableTypes);
            context.include("/WEB-INF/_jsp/cms/selectImageLink.jsp");
            rdata.remove("fieldName");
            rdata.remove("className");
            rdata.remove("activeType");
            rdata.remove("availableTypes");
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(RequestData rdata) throws JspException {
        field = (ImageLinkField) pdata.ensureField(name, ImageLinkField.FIELDTYPE_IMAGELINK);
        try {
            JspWriter writer = getWriter();
            if (!field.getLink().isEmpty()) {
                writer.print(String.format(runtimeTag,
                        field.getLink(),
                        className.isEmpty() ? "" : "class=\"" + className + '\"',
                        field.getTarget().isEmpty() ? "" : "target=" + field.getTarget() + '"',
                        field.getImgId() > 0 ?
                                String.format(imageRuntimeTag,
                                        field.getImgId(),
                                        field.getAltText(),
                                        field.getAltText(),
                                        field.getWidth(),
                                        field.getHeight())
                                : "&nbsp;"));
            } else {
                writer.print(field.getImgId() > 0 ?
                        String.format(imageRuntimeTag,
                                field.getImgId(),
                                field.getAltText(),
                                field.getAltText(),
                                field.getWidth(),
                                field.getHeight())
                        : "&nbsp;");
            }
        } catch (Exception ignored) {
        }
    }

}
