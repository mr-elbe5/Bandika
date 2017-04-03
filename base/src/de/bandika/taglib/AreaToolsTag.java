/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.application.StringCache;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class AreaToolsTag extends BaseTag {

  private int id = 0;
  private int partId = 0;
  private String areaName = "";
  private boolean endTag = false;
  private boolean anyPartEditMode = false;
  private boolean partEditMode = false;

  public void setId(int id) {
    this.id = id;
  }

  public void setPartId(int partId) {
    this.partId = partId;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public void setEndTag(boolean endTag) {
    this.endTag = endTag;
  }

  public void setAnyPartEditMode(boolean anyPartEditMode) {
    this.anyPartEditMode = anyPartEditMode;
  }

  public void setPartEditMode(boolean partEditMode) {
    this.partEditMode = partEditMode;
  }

  private static String partEditTag = "" +
    "<div class=\"areatools\">" +
    "<button class=\"btn btn-micro btn-primary\" onclick=\"savePagePart(%s,'%s');\">%s</i></button>" +
    "<button class=\"btn btn-micro\" onclick=\"submitMethod('cancelEditPagePart');\">%s</button>" +
    "</div>";

  private static String editTagStart = "" +
    "<div class=\"areatools\">" +
    "<a class=\"btn btn-micro btn-tool\" onclick=\"document.form.partId.value = %s;$('#selectPart%s').modal();return false;\" title=\"%s\"><i class=\"icon-plus icon-white\"></i></a>";

  private static String editTagNotEnd = "" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_page?method=editPagePart&id=%s&partId=%s&areaName=%s\" title=\"%s\"><i class=\"icon-pencil icon-white\"></i></a>" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_page?method=movePagePart&id=%s&partId=%s&areaName=%s&dir=-1\" title=\"%s\"><i class=\"icon-arrow-up icon-white\"></i></a>" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_page?method=movePagePart&id=%s&partId=%s&areaName=%s&dir=1\" title=\"%s\"><i class=\"icon-arrow-down icon-white\"></i></a>" +
    "<a class=\"btn btn-micro btn-tool\" href=\"/_page?method=deletePagePart&id=%s&partId=%s&areaName=%s\" title=\"%s\"><i class=\"icon-remove icon-white\"></i></a>";

  private static String editTagEnd = "</div>";

  public int doStartTag() throws JspException {
    JspWriter writer = getWriter();
    try {
      if (partEditMode || !anyPartEditMode) {
        if (partEditMode) {
          writer.println(String.format(partEditTag,
            partId,
            areaName,
            StringCache.getHtml("ok"),
            StringCache.getHtml("cancel")));
        } else {
          writer.print(String.format(editTagStart,
            partId,
            areaName,
            StringCache.getHtml("new")
          ));
          if (!endTag) {
            writer.print(String.format(editTagNotEnd,
              id, partId, areaName, StringCache.getHtml("change"),
              id, partId, areaName, StringCache.getHtml("up"),
              id, partId, areaName, StringCache.getHtml("down"),
              id, partId, areaName, StringCache.getHtml("delete")
            ));
          }
          writer.println(editTagEnd);
        }
      }
    } catch (Exception e) {
      throw new JspException(e);
    }
    return SKIP_BODY;
  }

}
