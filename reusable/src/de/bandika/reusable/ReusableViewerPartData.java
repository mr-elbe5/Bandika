/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.reusable;

import de.bandika._base.XmlHelper;
import de.bandika.page.PagePartData;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReusableViewerPartData extends PagePartData {

  public static final String DATAKEY = "data|reusableviewerpart";

  protected int targetPartId = 0;

  public int getTargetPartId() {
    return targetPartId;
  }

  public void setTargetPartId(int targetPartId) {
    this.targetPartId = targetPartId;
  }

  @Override
  public void generateContentXml(Document doc, Element root) {
    Element elem = XmlHelper.createChild(doc, root, "partId");
    XmlHelper.createCDATA(doc, elem, Integer.toString(targetPartId));
  }

  @Override
  public void evaluateContentXml(Element root) {
    NodeList fieldNodes = XmlHelper.getChildNodes(root, "partId");
    if (fieldNodes.getLength() > 0) {
      Element child = (Element) fieldNodes.item(0);
      String s = XmlHelper.getCData(child);
      try {
        targetPartId = Integer.parseInt(s);
      } catch (Exception ignore) {
      }
    }
  }

  public boolean isComplete() {
    return true;
  }

  public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
    super.readPagePartRequestData(rdata, sdata);
    setTargetPartId(rdata.getParamInt("targetPartId"));
    return isComplete();
  }

}