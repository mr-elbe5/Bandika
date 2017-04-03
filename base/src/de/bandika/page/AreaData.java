/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.RequestData;
import de.bandika._base.SessionData;

import java.util.ArrayList;
import java.util.HashSet;

public class AreaData {

  protected String name;
  protected ArrayList<PagePartData> parts = new ArrayList<PagePartData>();

  public AreaData() {
  }

  public AreaData(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addSearchContent(StringBuffer buffer) {
    for (PagePartData part : parts) {
      part.addSearchContent(buffer);
    }
  }

  public ArrayList<PagePartData> getParts() {
    return parts;
  }

  public PagePartData ensurePart(String template, int ranking) {
    PagePartData part;
    for (PagePartData ppd : parts) {
      if (ppd.getRanking() == ranking && ppd.getPartTemplate().equals(template))
        return ppd;
    }
    part = PageController.getInstance().getNewPagePartData(template);
    if (part == null)
      return null;
    part.setId(PageBean.getInstance().getNextId());
    part.setArea(getName());
    part.setRanking(ranking);
    parts.add(part);
    return part;
  }

  public PagePartData getPart(int pid) {
    for (PagePartData pdata : parts) {
      if (pdata.getId() == pid)
        return pdata;
    }
    return null;
  }

  public void addPagePart(PagePartData part, int fromPartId) {
    boolean found = false;
    if (fromPartId != -1) {
      for (int i = 0; i < parts.size(); i++) {
        PagePartData ppd = parts.get(i);
        if (ppd.getId() == fromPartId) {
          parts.add(i, part);
          found = true;
          break;
        }
      }
    }
    if (!found)
      parts.add(part);
    for (int i = 0; i < parts.size(); i++) {
      parts.get(i).setRanking(i + 1);
    }
  }

  public void movePagePart(int id, int dir) {
    for (int i = 0; i < parts.size(); i++) {
      PagePartData ppd = parts.get(i);
      if (ppd.getId() == id) {
        parts.remove(i);
        int idx = i + dir;
        if (idx > parts.size() - 1)
          parts.add(ppd);
        else if (idx < 0)
          parts.add(0, ppd);
        else
          parts.add(idx, ppd);
        break;
      }
    }
    for (int i = 0; i < parts.size(); i++) {
      parts.get(i).setRanking(i + 1);
    }
  }

  public void removePagePart(int id) {
    for (int i = 0; i < parts.size(); i++) {
      PagePartData ppd = parts.get(i);
      if (ppd.getId() == id) {
        parts.remove(i);
        return;
      }
    }
  }


  public void getFileUsage(HashSet<Integer> list) {
    for (PagePartData part : parts) {
      part.getFileUsage(list);
    }
  }

  public void getPageUsage(HashSet<Integer> list) {
    for (PagePartData part : parts) {
      part.getPageUsage(list);
    }
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    for (PagePartData part : parts) {
      part.prepareSave(rdata, sdata);
    }
  }

}
