/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika._base.XmlHelper;
import de.bandika._base.SearchHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;

/**
 * Class HtmlField is the data class for editable Fields used with a wysiwyg
 * HTML editor (FCKeditor). <br>
 * Usage:
 */
public class HtmlField extends BaseField {

  protected String html = "";

  public void setHtml(String html) {
    this.html = html;
  }

  public String getHtml() {
    return html;
  }

  @Override
  public Element generateXml(Document doc, Element parent) {
    Element elem = super.generateXml(doc, parent);
    XmlHelper.createCDATA(doc, elem, html);
    return elem;
  }

  @Override
  public void evaluateXml(Element node) {
    super.evaluateXml(node);
    html = XmlHelper.getCData(node);
  }

  @Override
  public void addSearchContent(StringBuffer buffer) {
    String searchContent = SearchHelper.getSearchContentFromHtml(html);
    buffer.append(" ").append(searchContent).append(" ");
  }

  @Override
  public void getFileUsage(HashSet<Integer> list) {
    int start;
    int end = 0;
    String fileStr = "/_file?method=show&amp;fid=";
    while (true) {
      start = html.indexOf(fileStr, end);
      if (start == -1)
        break;
      start += fileStr.length();
      end = html.indexOf("\"", start);
      if (end == -1)
        return;
      try {
        int fid = Integer.parseInt(html.substring(start, end));
        list.add(fid);
      } catch (Exception ignored) {
      }
      end++;
    }
  }

  @Override
  public void getPageUsage(HashSet<Integer> list) {
    int start;
    int end = 0;
    String docStr = "/_page?method=show&amp;id=";
    while (true) {
      start = html.indexOf(docStr, end);
      if (start == -1)
        break;
      start += docStr.length();
      end = html.indexOf("\"", start);
      if (end == -1)
        return;
      try {
        int pid = Integer.parseInt(html.substring(start, end));
        list.add(pid);
      } catch (Exception ignored) {
      }
      end++;
    }
    docStr = ".";
    start = 0;
    while (true) {
      end = html.indexOf(".html", start);
      if (end == -1)
        return;
      start = html.lastIndexOf(docStr, end);
      if (start == -1)
        break;
      start += docStr.length();
      try {
        int pid = Integer.parseInt(html.substring(start, end));
        list.add(pid);
      } catch (Exception ignored) {
      }
      start = end + ".html".length() + 1;
    }
  }

}