/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.pagepart;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.XmlData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.page.SectionData;
import de.elbe5.page.PageData;
import de.elbe5.servlet.SessionReader;
import de.elbe5.template.TemplateAttributes;
import de.elbe5.template.TemplateCache;
import de.elbe5.template.TemplateData;
import de.elbe5.template.TemplateType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class PagePartData extends BaseIdData implements Comparable<PagePartData>, XmlData {

    protected int version = 0;
    protected int pageId = 0;
    protected String section = "";
    protected boolean shared = false;
    protected String shareName = "";
    protected int ranking = 0;
    protected String templateName;
    protected String cssClass = "";
    protected String content = "";
    protected PagePartContent[] contents=new PagePartContent[0];
    protected String script="";
    protected int currentContentIdx=0;
    protected Set<Integer> pageIds = null;

    public PagePartData() {
        setContentCount(1);
    }

    @Override
    public int compareTo(PagePartData part) {
        int val = ranking - part.ranking;
        if (val != 0) {
            return val;
        }
        return shareName.compareTo(part.shareName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public int getCurrentContentIdx() {
        return currentContentIdx;
    }

    public void setCurrentContentIdx(int currentContentIdx) {
        this.currentContentIdx = currentContentIdx;
    }

    public int getContentCount() {
        return contents.length;
    }

    public void setContentCount(int count){
        if (contents.length==count || count<1)
            return;
        PagePartContent[] arr=new PagePartContent[count];
        for (int i=0; i<contents.length && i<count;i++){
            arr[i]=contents[i];
        }
        for (int i=contents.length; i<count;i++){
            arr[i]=new PagePartContent();
        }
        contents=arr;
    }

    public void setPartContent(int idx, PagePartContent content) {
        if (contents.length>idx)
            contents[idx]=content;
    }

    public void setCurrentPartContent(PagePartContent content) {
        setPartContent(getCurrentContentIdx(),content);
    }

    public PagePartContent getPartContent(int idx) {
        return contents.length>idx ? contents[idx] : null;
    }

    public PagePartContent getCurrentPartContent() {

        return getPartContent(getCurrentContentIdx());
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setXmlContent(String content){
        this.content = content == null ? "" : content;
        evaluateXmlContent();
    }

    public String getXmlContent(){
        return content;
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public void copyContent(PagePartData part) {
        setContentCount(part.getContentCount());
        for (int i=0; i<contents.length; i++) {
            contents[i].copyContent(part.getPartContent(i));
        }
    }

    public void getNodeUsage(Set<Integer> list) {
        for (PagePartContent content : contents) {
            content.getNodeUsage(list);
        }
    }

    @Override
    public void prepareSave() throws Exception {
        generateXmlContent();
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return getCurrentPartContent().readPagePartRequestData(request);
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request) {
        return true;
    }

    /******************* HTML part *********************************/

    public void appendPartHtml(StringBuilder sb, TemplateAttributes attributes, SectionData section, PageData pageData, HttpServletRequest request) {
        boolean editMode = pageData.isEditMode();
        Locale locale = SessionReader.getSessionLocale(request);
        if (editMode) {
            writeEditPartStart(pageData.getEditPagePart(), section.getName(), pageData.getId(), sb, locale);
        }
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            partTemplate.fillTemplate(sb, pageData, this, request);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        if (editMode) {
            writeEditPartEnd(pageData.getEditPagePart(), section.getName(), attributes.getString("type"), pageData.getId(), sb, locale);
        }
    }

    private static String EDITSTARTCODE=
            "<div id=\"part_%s\" class = \"editPagePart\">\n"+
            "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">" +
            "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>" +
            "<input type = \"hidden\" name = \"pageId\" value = \"%s\"/>" +
            "<input type = \"hidden\" name = \"sectionName\" value = \"%s\"/>" +
            "<input type = \"hidden\" name = \"partId\" value = \"%s\"/>\n"+
            "<div class = \"buttonset editSection\">" +
            "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">%s</button>" +
            "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'%s'});\">%s</button>" +
            "</div>";

    protected void writeEditPartStart(PagePartData editPagePart, String sectionName, int pageId, StringBuilder sb, Locale locale) {
        if (editPagePart == null) {
            sb.append("<div title=\"").append(StringUtil.toHtml(getTemplateName())).append("(ID=").append(getId()).append(") - ").append(StringUtil.getHtml("_rightClickEditHint")).append("\" id=\"part_").append(getId()).append("\" class = \"viewPagePart contextSource\">\n");
        } else if (this == editPagePart) {
            sb.append(String.format(EDITSTARTCODE,
                    getId(),
                    pageId,
                    sectionName,
                    getId(),
                    getHtml("_ok", locale),
                    pageId, getHtml("_cancel", locale)
            ));
        } else {
            sb.append("<div class = \"viewPagePart\">\n");
        }
    }

    private static String CONTEXTCODE=
                "<div class=\"icn isetting\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openEditPagePartSettings&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n" +
                "<div class=\"icn icode\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openSetVisibleContentIdx&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n"+
                "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s');\">%s</div>\n"+
                "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s&below=true');\">%s</div>\n"+
                "<div class=\"icn ishare\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openSharePagePart&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n"+
                "<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=-1');\">%s</div>\n"+
                "<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=1');\">%s</div>\n"+
                "<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.srv?',{act:'deletePagePart',pageId:'%s',sectionName:'%s',partId:'%s'});\">%s</div>\n";

    public void writeEditPartEnd(PagePartData editPagePart, String sectionName, String sectionType, int pageId, StringBuilder sb, Locale locale) {
        boolean staticSection = sectionName.equals(PageData.STATIC_SECTION_NAME);
        sb.append("</div>");
        if (editPagePart == null) {
            sb.append("<div class = \"contextMenu\">");
            sb.append("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'").append(pageId).append("',sectionName:'").append(sectionName).append("',partId:'").append(getId()).append("'})\">").append(getHtml("_edit", locale)).append("</div>\n");
            if (!staticSection) {
                sb.append(String.format(CONTEXTCODE,
                        getHtml("_settings", locale), pageId, sectionName, getId(), getHtml("_settings", locale),
                        getHtml("_visibleContent", locale), pageId, sectionName, getId(), getHtml("_visibleContent", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newAbove", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newBelow", locale),
                        getHtml("_share", locale), pageId, sectionName, getId(), getHtml("_share", locale),
                        pageId, sectionName, getId(), getHtml("_up", locale),
                        pageId, sectionName, getId(), getHtml("_down", locale),
                        pageId, sectionName, getId(), getHtml("_delete", locale)
                        ));
            }
            sb.append("</div>\n");
        } else if (this == editPagePart) {
            sb.append("</form>\n");
        }
    }

    public String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

    /******************* XML part *********************************/

    public void getXmlAttributes(Element node) {
        setShared(XmlUtil.getBooleanAttribute(node, "shared"));
        setShareName(XmlUtil.getStringAttribute(node, "shareName"));
        setRanking(XmlUtil.getIntAttribute(node, "ranking"));
        setTemplateName(XmlUtil.getStringAttribute(node, "templateName"));
        setCssClass(XmlUtil.getStringAttribute(node, "cssClass"));

    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
        XmlUtil.addBooleanAttribute(xmlDoc, node, "shared", isShared());
        XmlUtil.addAttribute(xmlDoc, node, "shareName", StringUtil.toXml(getShareName()));
        XmlUtil.addIntAttribute(xmlDoc, node, "ranking", getRanking());
        XmlUtil.addAttribute(xmlDoc, node, "templateName", StringUtil.toXml(getTemplateName()));
        XmlUtil.addAttribute(xmlDoc, node, "cssClass", StringUtil.toXml(getCssClass()));
        XmlUtil.addIntAttribute(xmlDoc, node, "count", getContentCount());
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode==null? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        addXmlAttributes(xmlDoc, node);
        for (int idx=0; idx<getContentCount();idx++) {
            PagePartContent partContent=contents[idx];
            partContent.setIdx(idx);
            partContent.toXml(xmlDoc,node);
        }
        Element scriptNode = XmlUtil.addNode(xmlDoc, node, "script");
        XmlUtil.addCDATA(xmlDoc, scriptNode, getScript());
        return node;
    }

    public void fromXml(Element node){
        if (node==null)
            return;
        getXmlAttributes(node);
        setContentCount(XmlUtil.getIntAttribute(node,"count"));
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                int idx=XmlUtil.getIntAttribute(child,"idx");
                PagePartContent content=new PagePartContent();
                content.setPagePartId(getId());
                content.fromXml(child);
                contents[idx]=content;
            }
            if (child.getTagName().equals("script")) {
                setScript(XmlUtil.getCData(child));
            }
        }
    }

    public void evaluateXmlContent() {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        Document doc = XmlUtil.getXmlDocument(content, "UTF-8");
        if (doc == null) {
            return;
        }
        Element root = XmlUtil.getRootNode(doc);
        if (root == null) {
            return;
        }
        fromXml(root);
    }

    public void generateXmlContent() {
        Document xmlDoc = XmlUtil.createXmlDocument();
        XmlUtil.createRootNode(xmlDoc, "part");
        toXml(xmlDoc, null);
        content = XmlUtil.xmlToString(xmlDoc);
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        for (PagePartContent content : contents) {
            content.appendSearchText(sb);
        }
    }

}
