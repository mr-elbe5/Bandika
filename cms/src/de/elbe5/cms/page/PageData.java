/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.DataProperties;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.webserver.tree.ResourceNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class PageData extends ResourceNode {
    public static final String STATIC_AREA_NAME = "static";
    protected String templateName = "";
    protected boolean isDefaultPage = false;
    protected Map<String, AreaData> areas = new HashMap<>();
    protected PagePartData editPagePart = null;

    public PageData() {
    }

    public PageData(PageData source) {
        copyFromTree(source);
    }

    protected void copyFromTree(PageData data) {
        super.copyFromTree(data);
        setTemplateName(data.getTemplateName());
        setDefaultPage(data.isDefaultPage());
    }

    @Override
    public String getUrl() {
        return path + ".html";
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, boolean withContent) {
        Element node = getElement(xmlDoc, parentNode, "page");
        XmlUtil.addAttribute(xmlDoc, node,"templateName", StringUtil.toXml(getTemplateName()));
        XmlUtil.addBooleanAttribute(xmlDoc, node,"defaultPage", isDefaultPage());
        for (AreaData area : areas.values()) {
            area.fillTreeXml(xmlDoc,node, withContent);
        }
    }

    public boolean isDefaultPage() {
        return isDefaultPage;
    }

    public void setDefaultPage(boolean defaultPage) {
        isDefaultPage = defaultPage;
    }

    public int getSiteId() {
        return getParentId();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public HashSet<Integer> getFileUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (AreaData area : areas.values()) {
            area.getFileUsage(list);
        }
        return list;
    }

    public HashSet<Integer> getPageUsage() {
        HashSet<Integer> list = new HashSet<>();
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
        return ensureArea(PageData.STATIC_AREA_NAME);
    }

    public Map<String, AreaData> getAreas() {
        return areas;
    }

    public PagePartData ensureStaticPart(String templateName, int ranking) {
        return getStaticArea().ensurePart(templateName, ranking);
    }

    public void sortPageParts() {
        for (AreaData area : areas.values()) {
            area.sortPageParts();
        }
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

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        AreaData area = getArea(part.getArea());
        if (area == null) {
            area = new AreaData(part.getArea());
            areas.put(part.getArea(), area);
        }
        area.addPagePart(part, fromPartId, below, setRanking);
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

    public void shareChanges(PagePartData part) {
        part.generateContent();
        for (AreaData area : areas.values()) {
            area.shareChanges(part);
        }
    }

    public void prepareSave(HttpServletRequest request) throws Exception {
        super.prepareSave();
        for (AreaData area : areas.values()) {
            area.prepareSave(request);
        }
        setAuthorName(SessionHelper.getUserName(request));
    }

    protected void fillProperties(DataProperties properties, Locale locale){
        super.fillProperties(properties, locale);
        properties.setKeyHeader("_page", locale);
        properties.addKeyProperty("_pageTemplate", getTemplateName(), locale);
    }

}