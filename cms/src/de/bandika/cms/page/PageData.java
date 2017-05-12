/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.log.Log;
import de.bandika.base.search.ISearchTextProvider;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.servlet.RequestReader;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.template.TemplateType;
import de.bandika.cms.tree.ResourceNode;
import de.bandika.cms.tree.TreeCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PageData extends ResourceNode implements ISearchTextProvider {

    public static final String STATIC_SECTION_NAME = "static";
    protected String templateName = "";
    protected boolean isDefaultPage = false;
    protected Map<String, SectionData> sections = new HashMap<>();

    protected boolean editMode = false;
    protected PagePartData editPagePart = null;

    public PageData() {
    }

    public PageData(PageData source) {
        copy(source);
    }

    public void copy(PageData data) {
        super.copy(data);
        setTemplateName(data.getTemplateName());
        setDefaultPage(data.isDefaultPage());
    }

    public void cloneData(PageData data) {
        super.cloneData(data);
        setTemplateName(data.getTemplateName());
        setDefaultPage(false);
        for (String sectionName : data.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.cloneData(data.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    @Override
    public String getUrl() {
        return path + ".html";
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

    public HashSet<Integer> getNodeUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (SectionData section : sections.values()) {
            section.getNodeUsage(list);
        }
        return list;
    }

    public SectionData ensureSection(String sectionName) {
        if (!sections.containsKey(sectionName)) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.setName(sectionName);
            sections.put(sectionName, section);
            return section;
        }
        return sections.get(sectionName);
    }

    public SectionData getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public SectionData getStaticSection() {
        return ensureSection(PageData.STATIC_SECTION_NAME);
    }

    public Map<String, SectionData> getSections() {
        return sections;
    }

    public PagePartData ensureStaticPart(String templateName, int ranking) {
        return getStaticSection().ensurePart(templateName, ranking);
    }

    public void sortPageParts() {
        for (SectionData section : sections.values()) {
            section.sortPageParts();
        }
    }

    public PagePartData getPagePart(String sectionName, int pid) {
        SectionData section = getSection(sectionName);
        return section.getPart(pid);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void readPageCreateRequestData(HttpServletRequest request) {
        setDisplayName(RequestReader.getString(request, "displayName").trim());
        String name = RequestReader.getString(request, "name").trim();
        if (name.isEmpty()) {
            name = getDisplayName();
        } else {
            int pos = name.lastIndexOf('.');
            if (pos != -1 && name.substring(pos).toLowerCase().startsWith(".htm")) {
                name = name.substring(0, pos);
            }
        }
        setName(name.isEmpty() ? getDisplayName() : name);
    }

    public void readPageSettingsRequestData(HttpServletRequest request) {
        readResourceNodeRequestData(request);
        setTemplateName(RequestReader.getString(request, "templateName"));
    }

    public void prepareEditing() throws Exception {
        setEditMode(true);
    }

    public void stopEditing() throws Exception {
        setEditMode(false);
        setEditPagePart(null);
    }

    public PagePartData getEditPagePart() {
        return editPagePart;
    }

    public void setEditPagePart(PagePartData editPagePart) {
        this.editPagePart = editPagePart;
    }

    public void setEditPagePart(String sectionName, int id) {
        setEditPagePart(getPagePart(sectionName, id));
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        SectionData section = getSection(part.getSection());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSection());
            sections.put(part.getSection(), section);
        }
        section.addPagePart(part, fromPartId, below, setRanking);
    }

    public void movePagePart(String sectionName, int id, int dir) {
        editPagePart = null;
        SectionData section = getSection(sectionName);
        section.movePagePart(id, dir);
    }

    public void removePagePart(String sectionName, int id) {
        SectionData section = getSection(sectionName);
        section.removePagePart(id);
        editPagePart = null;
    }

    public void shareChanges(PagePartData part) {
        part.generateXmlContent();
        for (SectionData section : sections.values()) {
            section.shareChanges(part);
        }
    }

    public void prepareCopy() throws Exception {
        for (SectionData section : sections.values()) {
            section.prepareCopy(getId());
        }
    }

    public void prepareSave() throws Exception {
        super.prepareSave();
        for (SectionData section : sections.values()) {
            section.prepareSave();
        }
    }

    /******************* HTML part *********************************/

    public String getPageHtml(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        TemplateData masterTemplate = TemplateCache.getInstance().getTemplate(TemplateType.MASTER, TreeCache.getInstance().getSite(getParentId()).getTemplateName());
        try {
            masterTemplate.fillTemplate(sb, this, null, request);
        } catch (Exception e) {
            Log.error("could not get page html", e);
        }
        return sb.toString();
    }

    public String getContentHtml(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        appendInnerContentHtml(sb, request);
        return sb.toString();
    }

    public String getPartContentHtml(HttpServletRequest request) {
        if (editPagePart != null)
            return editPagePart.getContentHtml(this, request);
        return "";
    }

    public void appendContentHtml(StringBuilder sb, HttpServletRequest request) {
        if (isEditMode()) {
            sb.append("<div id=\"pageContent\" class=\"editArea\">");
        } else {
            sb.append("<div id=\"pageContent\" class=\"viewArea\">");
        }
        appendInnerContentHtml(sb, request);
        if (isEditMode()) {
            sb.append("</div><script>$('#pageContent').initEditArea();</script>");
        } else {
            sb.append("</div>");
        }
    }

    public void appendInnerContentHtml(StringBuilder sb, HttpServletRequest request) {
        TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PAGE, getTemplateName());
        try {
            pageTemplate.fillTemplate(sb, this, null, request);
            if (editPagePart!=null){
                sb.append("<script>$('.editControl').hide();</script>");
            }
            else{
                sb.append("<script>$('.editControl').show();</script>");
            }
        } catch (Exception e) {
            Log.error("error in page template", e);
            sb.append("error");
        }
    }

    /******************* XML part *********************************/

    @Override
    protected Element getNewNode(Document xmlDoc) {
        return xmlDoc.createElement("page");
    }

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        PageBean.getInstance().loadPageContent(this, getMaxVersion());
        Element node = super.toXml(xmlDoc, parentNode);
        for (SectionData section : sections.values()) {
            section.toXml(xmlDoc, node);
        }
        return node;
    }

    @Override
    public void addXmlAttributes(Document xmlDoc, Element node) {
        super.addXmlAttributes(xmlDoc, node);
        XmlUtil.addAttribute(xmlDoc, node, "templateName", StringUtil.toXml(getTemplateName()));
        XmlUtil.addBooleanAttribute(xmlDoc, node, "defaultPage", isDefaultPage());
    }

    @Override
    public void getXmlAttributes(Element node) {
        super.getXmlAttributes(node);
        setTemplateName(XmlUtil.getStringAttribute(node, "templateName"));
        setDefaultPage(XmlUtil.getBooleanAttribute(node, "defaultPage"));
    }

    @Override
    public void fromXml(Element node) throws ParseException {
        super.fromXml(node);
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("section")) {
                SectionData section = new SectionData();
                section.setPageId(getId());
                section.fromXml(child);
                sections.put(section.getName(), section);
            }
        }
    }

    /******************* search part *********************************/

    @Override
    public String getSearchText() {
        StringBuilder sb = new StringBuilder();
        for (SectionData section : sections.values()) {
            section.appendSearchText(sb);
        }
        return sb.toString();
    }

}
