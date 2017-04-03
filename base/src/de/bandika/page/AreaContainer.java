/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.BaseData;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;

import java.util.*;

public class AreaContainer extends BaseData {

  public final static String DATAKEY = "data|areacontainer";

  public static final String STATIC_AREA_NAME = "static";

  HashMap<String, AreaData> areas = new HashMap<String, AreaData>();
  protected PagePartData editPagePart = null;
  protected boolean contentChanged = false;

  public AreaContainer() {
  }

  public void clearContent() {
    areas.clear();
    editPagePart = null;
    contentChanged = true;
  }

  public boolean isContentChanged() {
    return contentChanged;
  }

  public HashSet<Integer> getFileUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (AreaData area : areas.values()) {
      area.getFileUsage(list);
    }
    return list;
  }

  public HashSet<Integer> getPageUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (AreaData area : areas.values()) {
      area.getPageUsage(list);
    }
    return list;
  }

  public AreaData ensureArea(String areaName) {
    if (!areas.containsKey(areaName)) {
      AreaData area = new AreaData(areaName);
      areas.put(areaName, area);
      return area;
    }
    return areas.get(areaName);
  }

  public AreaData getArea(String areaName) {
    return areas.get(areaName);
  }

  public AreaData getStaticArea() {
    return ensureArea(AreaContainer.STATIC_AREA_NAME);
  }

  public HashMap<String, AreaData> getAreas() {
    return areas;
  }

  public PagePartData ensureStaticPart(String template, int ranking) {
    return getStaticArea().ensurePart(template, ranking);
  }

  public PagePartData getPagePart(int pid) {
    PagePartData data = null;
    for (AreaData area : areas.values()) {
      data = area.getPart(pid);
      if (data != null)
        break;
    }
    return data;
  }

  public PagePartData getPagePart(String areaName, int pid) {
    AreaData area = getArea(areaName);
    return area.getPart(pid);
  }

  public PagePartData getEditPagePart() {
    return editPagePart;
  }

  public void setEditPagePart(PagePartData editPagePart) {
    this.editPagePart = editPagePart;
  }

  public void setEditPagePart(String areaName, int id) {
    setEditPagePart(getPagePart(areaName, id));
  }

  public void addPagePart(PagePartData part, int fromPartId) {
    AreaData area = getArea(part.getArea());
    if (area == null) {
      area = new AreaData(part.getArea());
      areas.put(part.getArea(), area);
    }
    area.addPagePart(part, fromPartId);
  }

  public void movePagePart(String areaName, int id, int dir) {
    editPagePart = null;
    AreaData area = getArea(areaName);
    area.movePagePart(id, dir);
  }

  public void removePagePart(String areaName, int id) {
    AreaData area = getArea(areaName);
    area.removePagePart(id);
    editPagePart = null;
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    super.prepareSave(rdata, sdata);
    for (AreaData area : areas.values()) {
      area.prepareSave(rdata, sdata);
    }
  }

}