/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika.application.StringCache;
import de.bandika.cms.BaseField;
import de.bandika.cms.ImageField;
import de.bandika._base.*;
import de.bandika.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class ImageTag extends CmsBaseTag {

  ImageField field;

  protected String className;
  protected int preferredWidth = 0;

  public void setClassName(String className) {
    this.className = className;
  }

  public void setPreferredWidth(int preferredWidth) {
    this.preferredWidth = preferredWidth;
  }

  private static final String editTag = "" +
    "<div><input type=\"hidden\" id=\"%sImgId\" name=\"%sImgId\" value=\"%s\" />" +
    "<a href=\"#\" class=\"editField\" onclick=\"return openSetImage('%s',%s,%s);\">" +
    "<img class=\"editField %s\" id=\"%s\" %s />" +
    "</a></div>" +
    "<fieldset class=\"condensed\"><label>%s</label><input type=\"text\" id=\"%sWidth\" name=\"%sWidth\" value=\"%s\" />" +
    "<label>%s</label><input type=\"text\" class=\"editField\" id=\"%sHeight\" name=\"%sHeight\" value=\"%s\" />" +
    "<label>%s</label><input type=\"text\" class=\"editField\" id=\"%sAlt\" name=\"%sAlt\" value=\"%s\" /></fieldset>";
  private static final String filePart = "src=\"/_file?method=show&fid=%s\" alt=\"%s\" %s %s ";
  private static final String classAttr = "class=\"%s\"";
  private static final String widthAttr = "width=\"%s\"";
  private static final String heightAttr = "height=\"%s\"";
  private static final String dummyPart = "src=\"/_statics/images/dummy.gif\"";

  private static final String runtimeTag = "<img %s src=\"/_file?method=show&fid=%s\" alt=\"%s\" %s %s />";

  @Override
  protected void doEditTag(RequestData rdata) throws JspException {
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
    PageData data = (PageData) sdata.getParam("pageData");
    int pageId = data == null ? 0 : data.getId();
    field = (ImageField) pdata.ensureField(name, BaseField.FIELDTYPE_IMAGE);
    try {
      JspWriter writer = getWriter();
      writer.print(String.format(editTag,
        field.getIdentifier(),
        field.getIdentifier(),
        field.getImgId(),
        field.getIdentifier(),
        pageId,
        preferredWidth,
        !StringHelper.isNullOrEmtpy(className) ? className : "",
        field.getIdentifier(),
        field.getImgId() > 0 ?
          String.format(filePart,
            field.getImgId(),
            field.getAltText(),
            field.getWidth() > 0 ? String.format(widthAttr, field.getWidth()) : "",
            field.getHeight() > 0 ? String.format(heightAttr, field.getHeight()) : ""
          )
          : dummyPart,
        StringCache.getHtml("width"),
        field.getIdentifier(),
        field.getIdentifier(),
        field.getWidth(),
        StringCache.getHtml("height"),
        field.getIdentifier(),
        field.getIdentifier(),
        field.getHeight(),
        StringCache.getHtml("alt"),
        field.getIdentifier(),
        field.getIdentifier(),
        FormatHelper.toHtml(field.getAltText())
      ));
    } catch (Exception ignored) {
    }
  }

  @Override
  protected void doRuntimeTag(RequestData rdata) throws JspException {
    field = (ImageField) pdata.ensureField(name, BaseField.FIELDTYPE_IMAGE);
    try {
      JspWriter writer = getWriter();
      if (field.getImgId() > 0) {
        writer.print(String.format(runtimeTag,
          StringHelper.isNullOrEmtpy(className) ? "" : String.format(classAttr, className),
          field.getImgId(),
          field.getAltText(),
          field.getWidth() > 0 ? String.format(widthAttr, field.getWidth()) : "",
          field.getHeight() > 0 ? String.format(heightAttr, field.getHeight()) : ""));
      } else {
        writer.print("&nbsp;");
      }
    } catch (Exception ignored) {
    }
  }

}