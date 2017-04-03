/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika._base.FormatHelper;
import de.bandika.application.StringCache;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class FileUploadTag extends BaseTag {

  private String name = "";

  public void setName(String name) {
    this.name = name;
  }

  static String tag = "<div class=\"fileupload fileupload-new\" data-provides=\"fileupload\">" +
    "<div class=\"uneditable-input span3\">" +
    "<i class=\"icon-file fileupload-exists\"></i>" +
    "<span class=\"fileupload-preview\"></span>" +
    "</div>" +
    "<span class=\"btn btn-file\">" +
    "<span class=\"fileupload-new\">%s</span>" +
    "<span class=\"fileupload-exists\">%s</span>" +
    "<input type=\"file\" id=\"%s\" name=\"%s\" />" +
    "</span>" +
    "<a href=\"#\" class=\"btn fileupload-exists\" data-dismiss=\"fileupload\">%s</a>" +
    "</div>";

  public int doStartTag() throws JspException {
    JspWriter writer = getWriter();
    try {
      writer.print(String.format(tag,
        StringCache.getHtml("selectFile"),
        StringCache.getHtml("change"),
        FormatHelper.toHtml(name),
        FormatHelper.toHtml(name),
        StringCache.getHtml("remove")));
    } catch (Exception e) {
      throw new JspException(e);
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    return 0;
  }

}