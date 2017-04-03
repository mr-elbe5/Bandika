/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.RequestData;
import de.bandika._base.*;
import de.bandika._base.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashSet;

public class HtmlPartData extends PagePartData {

  public final static String DATAKEY = "data|htmlpart";

  protected String html = "";

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  @Override
  public void generateContentXml(Document doc, Element root) {
    Element elem = XmlHelper.createChild(doc, root, "html");
    XmlHelper.createCDATA(doc, elem, html);
  }

  @Override
  public void evaluateContentXml(Element root) {
    NodeList fieldNodes = XmlHelper.getChildNodes(root, "html");
    if (fieldNodes.getLength() > 0) {
      Element child = (Element) fieldNodes.item(0);
      html = XmlHelper.getCData(child);
    }
  }

  @Override
  public void addSearchContent(StringBuffer buffer) {
    String searchContent = SearchHelper.getSearchContentFromHtml(html);
    buffer.append(" ").append(searchContent).append(" ");
  }

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

  public void getPageUsage(HashSet<Integer> list) {

  }

  public boolean isComplete() {
    return true;
  }

  public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
    super.readPagePartRequestData(rdata, sdata);
    setHtml(rdata.getParamString("htmlArea"));
    return isComplete();
  }

}