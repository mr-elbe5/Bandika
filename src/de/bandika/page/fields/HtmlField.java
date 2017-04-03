/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page.fields;

import de.bandika.base.*;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.HashSet;

/**
 * Class HtmlField is the data class for editable Fields used with a wysiwyg HTML editor (FCKeditor).  <br>
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
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    html = rdata.getParamString(getIdentifier());
  }

	@Override
	public Element generateXml(Document doc, Element parent){
		Element elem= super.generateXml(doc,parent);
		XmlData.createCDATA(doc,elem,html);
		return elem;
	}

	@Override
	public void evaluateXml(Element node){
		super.evaluateXml(node);
		html=XmlData.getCData(node);
	}

  @Override
  public void getDocumentUsage(HashSet<Integer> list) {
    int start = 0;
    int end = 0;
    String docStr = "/srv25?ctrl=doc&amp;method=show&amp;did=";
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

  @Override
  public void getImageUsage(HashSet<Integer> list) {
    int start = 0;
    int end = 0;
    String imgStr = "/srv25?ctrl=img&amp;method=show&amp;iid=";
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

}