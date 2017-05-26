/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.template.TemplateType;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MultiHtmlPartData extends HtmlPartData {

    protected PagePartContent[] contents = new PagePartContent[0];
    protected String containerName = "";
    protected String script = "";
    protected int currentContentIdx = 0;

    public MultiHtmlPartData() {
        setContentCount(1);
    }

    public void cloneData(PagePartData data) {
        super.cloneData(data);
        if (data instanceof MultiHtmlPartData) {
            MultiHtmlPartData htmlData = (MultiHtmlPartData) data;
            setContainerName(htmlData.getContainerName());
            setScript(htmlData.getScript());
        }
    }

    public void copyContent(PagePartData part) {
        if (part instanceof MultiHtmlPartData) {
            MultiHtmlPartData htmlData = (MultiHtmlPartData) part;
            setContentCount(htmlData.getContentCount());
            for (int i = 0; i < contents.length; i++) {
                contents[i].copyContent(htmlData.getPartContent(i));
            }
        }
    }

    public String getHtmlId() {
        return getSectionName() + "-part-" + getId() + "." + getCurrentContentIdx();
    }

    public String getContainerId() {
        return getSectionName() + "-cntr-" + getId();
    }

    public int getCurrentContentIdx() {
        return currentContentIdx;
    }

    public void setCurrentContentIdx(int currentContentIdx) {
        this.currentContentIdx = currentContentIdx;
    }

    public void changeCurrentContentIdx(int dir) {
        currentContentIdx = currentContentIdx + dir;
        if (currentContentIdx < 0)
            currentContentIdx = contents.length - 1;
        else if (currentContentIdx >= contents.length)
            currentContentIdx = 0;
    }

    public int getContentCount() {
        return contents.length;
    }

    public void setContentCount(int count) {
        if (contents.length == count || count < 1)
            return;
        PagePartContent[] arr = new PagePartContent[count];
        for (int i = 0; i < contents.length && i < count; i++) {
            arr[i] = contents[i];
        }
        for (int i = contents.length; i < count; i++) {
            arr[i] = new PagePartContent();
        }
        contents = arr;
    }

    public PagePartContent getPartContent(int idx) {
        return contents.length > idx ? contents[idx] : null;
    }

    public PagePartContent getPartContent() {
        return getPartContent(getCurrentContentIdx());
    }

    public Field ensureField(String name, String fieldType) {
        return getPartContent().ensureField(name, fieldType);
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void getNodeUsage(Set<Integer> list) {
        for (PagePartContent content : contents) {
            content.getNodeUsage(list);
        }
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        super.readPagePartSettingsData(request);
        setContentCount(RequestReader.getInt(request, "contentCount"));
        setContainerName(RequestReader.getString(request, "containerName"));
        setScript(RequestReader.getString(request, "script"));
    }

    public void readPagePartVisibilityData(HttpServletRequest request) {
        changeCurrentContentIdx(RequestReader.getInt(request, "dir"));
    }

    /******************* HTML part *********************************/

    public void appendLivePartHtml(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData) throws IOException {
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            writer.write("<div class=\"pagePart\" id=\"" + getHtmlId() + "\" >");
            writer.write("<div id=\"" + getContainerId() + "\">");
            for (int i = 0; i < getContentCount(); i++) {
                setCurrentContentIdx(i);
                partTemplate.writeTemplate(context, writer, request, pageData, this);
            }
            setCurrentContentIdx(0);
            writer.write("</div>");
            writer.write("<script type=\"text/javascript\">" + getScript().replace(TemplateData.PLACEHOLDER_CONTAINERID, getContainerId()) + ";</script>");
            writer.write("</div>");
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
    }

    protected void appendContextCode(JspWriter writer, String sectionType, Locale locale) throws IOException {
        writer.write("<div class=\"icn isetting\" onclick = \"return openLayerDialog('" + getHtml("_settings", locale) + "', '/pageedit.ajx?act=openEditMultiHtmlPartSettings&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "');\">" + getHtml("_settings", locale) + "</div>\n" + "<div class=\"icn iup\" onclick = \"return linkTo('/pageedit.ajx?act=setVisibleContentIdx&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "&dir=-1');\">" + getHtml("_previousContent", locale) + "</div>\n" + "<div class=\"icn idown\" onclick = \"return linkTo('/pageedit.ajx?act=setVisibleContentIdx&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "&dir=1');\">" + getHtml("_nextContent", locale) + "</div>\n" + "<div class=\"icn inew\" onclick = \"return openLayerDialog('" + getHtml("_addPart", locale) + "', '/pageedit.ajx?act=openAddPagePart&pageId=" + pageId + "&sectionName=" + sectionName + "&sectionType=" + sectionType + "&partId=" + getId() + "');\">" + getHtml("_newAbove", locale) + "</div>\n" + "<div class=\"icn inew\" onclick = \"return openLayerDialog('" + getHtml("_addPart", locale) + "', '/pageedit.ajx?act=openAddPagePart&pageId=" + pageId + "&sectionName=" + sectionName + "&sectionType=" + sectionType + "&partId=" + getId() + "&below=true');\">" + getHtml("_newBelow", locale) + "</div>\n" + "<div class=\"icn ishare\" onclick = \"return openLayerDialog('" + getHtml("_share", locale) + "', '/pageedit.ajx?act=openSharePagePart&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "');\">" + getHtml("_share", locale) + "</div>\n" + "<div class=\"icn iup\" onclick = \"return linkTo('/pageedit.ajx?act=movePagePart&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "&dir=-1');\">" + getHtml("_up", locale) + "</div>\n" + "<div class=\"icn idown\" onclick = \"return linkTo('/pageedit.ajx?act=movePagePart&pageId=" + pageId + "&sectionName=" + sectionName + "&partId=" + getId() + "&dir=1');\">" + getHtml("_down", locale) + "</div>\n" + "<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pageedit.ajx?',{act:'deletePagePart',pageId:'" + pageId + "',sectionName:'" + sectionName + "',partId:'" + getId() + "'});\">" + getHtml("_delete", locale) + "</div>\n");
    }

    /******************* XML part *********************************/

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        XmlUtil.addAttribute(xmlDoc, node, "containerName", StringUtil.toXml(getContainerName()));
        XmlUtil.addIntAttribute(xmlDoc, node, "count", getContentCount());
        for (int idx = 0; idx < getContentCount(); idx++) {
            PagePartContent partContent = contents[idx];
            partContent.setIdx(idx);
            partContent.toXml(xmlDoc, node);
        }
        Element scriptNode = XmlUtil.addNode(xmlDoc, node, "script");
        XmlUtil.addCDATA(xmlDoc, scriptNode, getScript());
        return node;
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        setContainerName(XmlUtil.getStringAttribute(node, "containerName"));
        setContentCount(XmlUtil.getIntAttribute(node, "count"));
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                int idx = XmlUtil.getIntAttribute(child, "idx");
                PagePartContent content = new PagePartContent();
                content.setPagePartId(getId());
                content.fromXml(child);
                contents[idx] = content;
            }
            if (child.getTagName().equals("script")) {
                setScript(XmlUtil.getCData(child));
            }
        }
    }

}
