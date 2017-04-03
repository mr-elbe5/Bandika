/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika.cms.ImageField;
import de.bandika.servlet.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ImageTag extends CmsBaseTag {

    protected String className="";

    protected ImageField field;

    public void setClassName(String className) {
        this.className = className;
    }

    private static final String runtimeTag = "<img class=\"%s\" src=\"/image.srv?act=show&fid=%s\" alt=\"%s\" title=\"%s\" style=\"width:%s; height:%s\" />";

    @Override
    protected void doEditTag(RequestData rdata) throws JspException {
        field = (ImageField) pdata.ensureField(name, ImageField.FIELDTYPE_IMAGE);
        try {
            rdata.put("fieldName", name);
            rdata.put("className", className);
            context.include("/WEB-INF/_jsp/cms/selectImage.jsp");
            rdata.remove("fieldName");
            rdata.remove("className");
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(RequestData rdata) throws JspException {
        field = (ImageField) pdata.ensureField(name, ImageField.FIELDTYPE_IMAGE);
        try {
            JspWriter writer = getWriter();
            if (field.getImgId() > 0) {
                writer.print(String.format(runtimeTag,
                        className,
                        field.getImgId(),
                        field.getAltText(),
                        field.getAltText(),
                        field.getWidth(),
                        field.getHeight()));
            } else {
                writer.print("&nbsp;");
            }
        } catch (Exception ignored) {
        }
    }

}
