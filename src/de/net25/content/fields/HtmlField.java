/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content.fields;

import de.net25.resources.statics.Statics;
import de.net25.base.XmlData;
import de.net25.base.Formatter;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.HashSet;
import java.util.Locale;

/**
 * Class HtmlField is the data class for editable Fields used with a wysiwyg HTML editor (FCKeditor).  <br>
 * Usage:
 */
public class HtmlField extends BaseField {

  protected String html = "";

  /**
   * Method setHtml sets the html of this HtmlField object.
   *
   * @param html the html of this HtmlField object.
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * Method addNodes
   *
   * @param buffer of type StringBuffer
   */
  protected void addNodes(StringBuffer buffer) {
    super.addNodes(buffer);
    XmlData.addNode(buffer, "html", html);
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
    super.readNodes();
    html = XmlData.getNode(xml, "html");
  }

  /**
   * Method getHtml returns the html of this BaseField object.
   *
   * @return the html (type String) of this BaseField object.
   */
  @Override
  public String getHtml(Locale locale) {
    if (html.length() == 0)
      return "&nbsp;";
    return html;
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  @Override
  public String getEditHtml(Locale locale) {
    StringBuffer buffer = new StringBuffer("<script type=\"text/javascript\" src=\"");
    buffer.append(Statics.FCK_PATH);
    buffer.append("fckeditor.js\"></script>");
    buffer.append("<script type=\"text/javascript\">");
    buffer.append("var fck=new FCKeditor('");
    buffer.append(getIdentifier());
    buffer.append("','100%','500');");
    buffer.append("fck.BasePath='");
    buffer.append(Statics.FCK_PATH);
    buffer.append("';");
    buffer.append("fck.Config['DefaultLanguage']='");
    buffer.append(Statics.getLanguage(locale));
    buffer.append("';");
    buffer.append("fck.Config['LinkBrowserURL']='");
    buffer.append(Statics.DYNAMIC_BASE + "srv25?ctrl=doc&method=openFckDocumentSelector");
    buffer.append("';");
    buffer.append("fck.Config['ImageBrowserURL']='");
    buffer.append(Statics.DYNAMIC_BASE + "srv25?ctrl=img&method=openFckImageSelector");
    buffer.append("';");
    buffer.append("fck.Value='");
    buffer.append(Formatter.toJS(html));
    buffer.append("';fck.Create();");
    buffer.append("</script>");
    return buffer.toString();
  }

  /**
   * Method getDocumentUsage
   *
   * @param list of type HashSet<Integer>
   */
  @Override
  public void getDocumentUsage(HashSet<Integer> list) {
    int start = 0;
    int end = 0;
    String docStr = Statics.DYNAMIC_BASE + "srv25?ctrl=" + Statics.KEY_DOCUMENT + "&amp;id=";
    while (true) {
      start = html.indexOf(docStr, end);
      if (start == -1)
        return;
      start += docStr.length();
      end = html.indexOf("\"", start);
      if (end == -1)
        return;
      try {
        int did = Integer.parseInt(html.substring(start, end));
        list.add(did);
      }
      catch (Exception e) {
      }
      end++;
    }
  }

  /**
   * Method getImageUsage
   *
   * @param list of type HashSet<Integer>
   */
  @Override
  public void getImageUsage(HashSet<Integer> list) {
    int start = 0;
    int end = 0;
    String imgStr = Statics.DYNAMIC_BASE + "srv25?ctrl=" + Statics.KEY_IMAGE + "&amp;id=";
    while (true) {
      start = html.indexOf(imgStr, end);
      if (start == -1)
        return;
      start += imgStr.length();
      end = html.indexOf("\"", start);
      if (end == -1)
        return;
      try {
        int iid = Integer.parseInt(html.substring(start, end));
        list.add(iid);
      }
      catch (Exception e) {
      }
      end++;
    }
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   */
  @Override
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    html = rdata.getParamString(getIdentifier());
  }

}