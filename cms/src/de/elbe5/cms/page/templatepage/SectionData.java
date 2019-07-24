/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SectionData {

    protected String name = "";
    protected int pageId = 0;
    protected boolean flex;
    protected boolean inTemplate = false;
    protected List<PagePartData> parts = new ArrayList<>();

    public SectionData() {
    }

    public void cloneData(SectionData data) {
        setName(data.getName());
        for (PagePartData srcPart : data.parts) {
            PagePartData part = PagePartFactory.getPagePartData(data.getClass().getSimpleName());
            if (part == null)
                continue;
            part.setSectionName(getName());
            part.cloneData(srcPart);
            parts.add(part);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public boolean isFlex() {
        return flex;
    }

    public void setFlex(boolean flex) {
        this.flex = flex;
    }

    public boolean isInTemplate() {
        return inTemplate;
    }

    public void setInTemplate(boolean inTemplate) {
        this.inTemplate = inTemplate;
    }

    public String getCss() {
        return isFlex() ? "row" : "";
    }

    public List<PagePartData> getParts() {
        return parts;
    }

    public void sortPageParts() {
        Collections.sort(parts);
    }

    public PagePartData getPart(int pid) {
        for (PagePartData pdata : parts) {
            if (pdata.getId() == pid) {
                return pdata;
            }
        }
        return null;
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        boolean found = false;
        if (fromPartId != -1) {
            for (int i = 0; i < parts.size(); i++) {
                PagePartData ppd = parts.get(i);
                if (ppd.getId() == fromPartId) {
                    parts.add(below ? i + 1 : i, part);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            parts.add(part);
        }
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
                if (idx > parts.size() - 1) {
                    parts.add(ppd);
                } else if (idx < 0) {
                    parts.add(0, ppd);
                } else {
                    parts.add(idx, ppd);
                }
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

    public void getNodeUsage(HashSet<Integer> list) {
        for (PagePartData part : parts) {
            part.getNodeUsage(list);
        }
    }

    public void prepareCopy() {
        for (PagePartData part : parts) {
            part.prepareCopy();
        }
    }

}
