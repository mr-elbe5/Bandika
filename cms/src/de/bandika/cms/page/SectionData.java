/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.template.*;
import de.bandika.servlet.SessionReader;
import de.bandika.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.*;

public class SectionData {

    protected String name;
    protected String className = "";
    protected String type = "";
    protected List<PagePartData> parts = new ArrayList<>();

    int pageId = 0;

    public SectionData() {
    }

    public void cloneData(SectionData data) {
        setName(data.getName());
        for (PagePartData srcPart : data.parts) {
            PagePartData part = srcPart.getDataType().getNewPagePartData();
            part.setPageId(getPageId());
            part.setSection(getName());
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PagePartData> getParts() {
        return parts;
    }

    public void sortPageParts() {
        Collections.sort(parts);
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public PagePartData ensurePart(String templateName, int ranking) {
        PagePartData part;
        for (PagePartData ppd : parts) {
            if (ppd.getRanking() == ranking && ppd.getTemplateName().equals(templateName)) {
                return ppd;
            }
        }
        PartTemplateData template= (PartTemplateData)TemplateCache.getInstance().getTemplate(TemplateType.PART,templateName);
        part = template.getDataType().getNewPagePartData();
        part.setTemplateName(templateName);
        part.setId(PageBean.getInstance().getNextId());
        part.setSection(getName());
        part.setRanking(ranking);
        parts.add(part);
        return part;
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

    public void shareChanges(PagePartData part) {
        for (PagePartData ppd : parts) {
            if (ppd.getId() == part.getId() && part != ppd) {
                ppd.copyContent(part);
                return;
            }
        }
    }

    public void getNodeUsage(HashSet<Integer> list) {
        for (PagePartData part : parts) {
            part.getNodeUsage(list);
        }
    }

    public void prepareCopy(int pageId) throws Exception {
        for (PagePartData part : parts) {
            part.prepareCopy(pageId);
        }
    }

    public void prepareSave() throws Exception {
        for (PagePartData part : parts) {
            part.prepareSave();
        }
    }

    /******************* HTML part *********************************/

    public void appendSectionHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, PageData pageData) throws IOException {
        boolean editMode = pageData.isEditMode();
        Locale locale = SessionReader.getSessionLocale(request);
        String cls = attributes.getString("class");
        boolean hasParts = getParts().size() > 0;
        if (editMode) {
            writer.write("<div class = \"editSection\">");
            if (hasParts) {
                writer.write("<div class = \"editSectionHeader\">Section " + getName()+ "</div>");
            } else {
                writer.write("<div class = \"editSectionHeader empty contextSource\" title=\"" + StringUtil.getHtml("_rightClickEditHint") + "\">Section " + getName() + "</div>");
                writer.write("<div class = \"contextMenu\"><div class=\"icn inew\" onclick = \"return openLayerDialog('" + StringUtil.getHtml("_addPart", locale) + "', '/pagepart.ajx?act=openAddPagePart&pageId=" + pageData.getId() + "&sectionName=" + getName() + "&sectionType=" + attributes.getString("type") + "&partId=-1');\">" + StringUtil.getHtml("_new", locale) + "\n</div>\n</div>");
            }
        }
        if (!getParts().isEmpty()) {
            writer.write("<div class = \"section " + cls + "\">");
            for (PagePartData pdata : getParts()) {
                pdata.appendPartHtml(context, writer, request, attributes.getString("type"), pageData);
            }
            writer.write("</div>");
        }
        if (editMode) {
            writer.write("</div>");
        }
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        for (PagePartData part : parts) {
            part.appendSearchText(sb);
        }
    }

}
