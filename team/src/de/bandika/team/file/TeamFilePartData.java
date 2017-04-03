/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.file;

import de.bandika._base.RequestData;
import de.bandika._base.*;
import de.bandika._base.SessionData;
import de.bandika.team.TeamPartData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TeamFilePartData extends TeamPartData {

  public static final String DATAKEY = "data|teamfilepart";

  public final static int MODE_LIST = 0;
  public final static int MODE_EDIT = 1;
  public final static int MODE_DELETE = 2;
  public final static int MODE_HISTORY = 3;
  public final static int MODE_HISTORY_DELETE = 4;

  protected String title = "";

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public void generateContentXml(Document doc, Element root) {
    Element elem = XmlHelper.createChild(doc, root, "title");
    XmlHelper.createCDATA(doc, elem, title);
  }

  @Override
  public void evaluateContentXml(Element root) {
    NodeList fieldNodes = XmlHelper.getChildNodes(root, "title");
    if (fieldNodes.getLength() > 0) {
      Element child = (Element) fieldNodes.item(0);
      title = XmlHelper.getCData(child);
    }
  }

  public boolean isComplete() {
    return DataHelper.isComplete(title);
  }

  public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
    setTitle(rdata.getParamString("title"));
    return isComplete();
  }

}