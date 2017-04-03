/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika._base.*;
import de.bandika.cms.BaseField;
import de.bandika.cms.HtmlField;
import de.bandika.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class HtmlTag extends CmsBaseTag {

  HtmlField field;

  protected String toolbar = "Full";
  protected String height;

  public void setToolbar(String toolbar) {
    this.toolbar = toolbar;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  private static final String editTag = "" +
    "<textarea class=\"editField\" name=\"%s\" cols=\"80\" rows=\"10\" style=\"width:100%%\">%s</textarea>" +
    "<script type=\"text/javascript\">" +
    "CKEDITOR.replace('%s',{" +
    "  customConfig : '/_statics/script/editorConfig.js'," +
    "  toolbar: '%s'," +
    "  %s " +
    "  filebrowserBrowseUrl : '/_page?method=openSelectAsset&assetType=LINK&forHTML=1&type=page&availableTypes=page,document,image&id=%s'," +
    "  filebrowserImageBrowseUrl : '/_page?method=openSelectAsset&assetType=FILE&forHTML=1&type=image&availableTypes=image&id=%s'" +
    "});" +
    "</script>";
  private static final String heightAttr = "height: '%s',";

  @Override
  protected void doEditTag(RequestData rdata) throws JspException {
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
    PageData data = (PageData) sdata.getParam("pageData");
    int pageId = data == null ? 0 : data.getId();
    field = (HtmlField) pdata.ensureField(name, BaseField.FIELDTYPE_HTML);
    try {
      JspWriter writer = getWriter();
      writer.print(String.format(editTag,
        field.getIdentifier(),
        FormatHelper.toHtmlInput(field.getHtml()),
        field.getIdentifier(),
        toolbar,
        !StringHelper.isNullOrEmtpy(height) ?
          String.format(heightAttr, DataHelper.getCssSize(height)) :
          "",
        pageId,
        pageId
      ));
    } catch (Exception e) {
      Logger.error(getClass(), "error writing tag", e);
    }
  }

  @Override
  protected void doRuntimeTag(RequestData rdata) throws JspException {
    field = (HtmlField) pdata.ensureField(name, BaseField.FIELDTYPE_HTML);
    try {
      JspWriter writer = getWriter();
      if (field.getHtml().length() == 0)
        writer.print("&nbsp;");
      else
        writer.print(field.getHtml());
    } catch (Exception ignored) {
    }
  }

}