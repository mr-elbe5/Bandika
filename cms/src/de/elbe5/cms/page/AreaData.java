/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AreaData {
    protected String name;
    protected List<PagePartData> parts = new ArrayList<>();

    public AreaData(String name) {
        this.name = name;
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, boolean withContent) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "area");
        XmlUtil.addAttribute(xmlDoc, node,"name", StringUtil.toXml(getName()));
        for (PagePartData part : parts) {
            part.fillTreeXml(xmlDoc,node, withContent);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PagePartData> getParts() {
        return parts;
    }

    public void sortPageParts() {
        Collections.sort(parts);
    }

    public PagePartData ensurePart(String templateName, int ranking) {
        PagePartData part;
        for (PagePartData ppd : parts) {
            if (ppd.getRanking() == ranking && ppd.getTemplateName().equals(templateName)) return ppd;
        }
        part = PagePartData.getNewPagePartData(templateName);
        if (part == null) return null;
        part.setId(PageBean.getInstance().getNextId());
        part.setArea(getName());
        part.setRanking(ranking);
        parts.add(part);
        return part;
    }

    public PagePartData getPart(int pid) {
        for (PagePartData pdata : parts) {
            if (pdata.getId() == pid) return pdata;
        }
        return null;
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        boolean found = false;
        if (fromPartId != -1) {
            for (int i = 0; i < parts.size(); i++) {
                PagePartData ppd = parts.get(i);
                if (ppd.getId() == fromPartId) {
                    parts.add(below ? i+1 : i, part);
                    found = true;
                    break;
                }
            }
        }
        if (!found) parts.add(part);
        if (setRanking) {
            for (int i = 0; i < parts.size(); i++) {
                parts.get(i).setRanking(i + 1);
            }
        }
    }

    public void movePagePart(int id, int dir) {
        for (int i = 0; i < parts.size(); i++) {
            PagePartData ppd = parts.get(i);
            if (ppd.getId() == id) {
                parts.remove(i);
                int idx = i + dir;
                if (idx > parts.size() - 1) parts.add(ppd);
                else if (idx < 0) parts.add(0, ppd);
                else parts.add(idx, ppd);
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

    public void shareChanges(PagePartData part) {
        for (PagePartData ppd : parts) {
            if (ppd.getId() == part.getId() && part != ppd) {
                ppd.copyContent(part);
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

    public void prepareSave(HttpServletRequest request) throws Exception {
        for (PagePartData part : parts) {
            part.prepareSave();
        }
    }
}
