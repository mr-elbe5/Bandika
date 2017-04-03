/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.reusable;

import de.bandika.page.AreaContainer;
import de.bandika.page.PagePartData;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;

public class ReusablePartContainer extends AreaContainer {

  public static final String DATAKEY = "data|reusablepart";

  protected String wrapperTemplate = "";

  public ReusablePartContainer() {
  }

  public void addPagePart(PagePartData part) {
    part.setArea(AreaContainer.STATIC_AREA_NAME);
    addPagePart(part, -1);
  }

  public PagePartData getPagePart() {
    if (getStaticArea().getParts().size() > 0)
      return getStaticArea().getParts().get(0);
    return null;
  }

  public String getWrapperTemplate() {
    return wrapperTemplate;
  }

  public String getWrapperTemplateUrl() {
    return "/_jsp/_wrapperlayout/" + wrapperTemplate + ".jsp";
  }

  public void setWrapperTemplate(String wrapperTemplate) {
    this.wrapperTemplate = wrapperTemplate;
  }

  public boolean readRequestSettingsData(RequestData rdata, SessionData sdata) {
    PagePartData part = getPagePart();
    if (part != null) {
      part.setName(rdata.getParamString("name"));
      return part.isCompleteName(rdata);
    }
    return true;
  }

  public boolean readRequestContentData(RequestData rdata, SessionData sdata) {
    if (editPagePart != null) {
      return editPagePart.readPagePartRequestData(rdata, sdata);
    }
    return true;
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    PagePartData part = getPagePart();
    if (part != null) {
      part.prepareSave(rdata, sdata);
    }
  }

}