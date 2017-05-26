/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.pagepart;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.XmlData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.page.PageBean;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.SectionData;
import de.bandika.cms.template.PartTemplateDataType;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.template.TemplateType;
import de.bandika.servlet.SessionReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class PagePartData extends BaseIdData implements Comparable<PagePartData>, XmlData {

    protected int version = 0;
    protected int pageId = 0;
    protected String sectionName = "";
    protected boolean shared = false;
    protected String shareName = "";
    protected int ranking = 0;
    protected String templateName;
    protected boolean editable = false;
    protected boolean dynamic = false;
    protected String content = "";
    protected Set<Integer> pageIds = null;

    public PagePartData(){
    }

    public PartTemplateDataType getDataType(){
        return null;
    }

    public String getDataTypeName(){
        return getDataType()==null ? "" : getDataType().name();
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setTemplateName(data.getTemplateName());
        setEditable((data.isEditable()));
        setDynamic(data.isDynamic());
        content = data.content;
    }

    public void copyContent(PagePartData part) {
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return shareName.compareTo(data.shareName);
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

    public String getHtmlId() {
        return getSectionName() + "-part-" + getId();
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setTemplateData(TemplateData data){
        setTemplateName(data.getName());
        setEditable(data.isEditable());
        setDynamic(data.isDynamic());
    }

    public void prepareCopy(int pageId) throws Exception {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
        setPageId(pageId);
        setVersion(1);
    }

    public Field ensureField(String name, String fieldType) {
        return null;
    }

    @Override
    public void prepareSave() throws Exception {
        generateXmlContent();
    }

    public void generateXmlContent() {
        Document xmlDoc = XmlUtil.createXmlDocument();
        XmlUtil.createRootNode(xmlDoc, "part");
        toXml(xmlDoc, null);
        content = XmlUtil.xmlToString(xmlDoc);
    }

    public void setXmlContent(String content) {
        this.content = content == null ? "" : content;
        evaluateXmlContent();
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

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        return node;
    }

    public void fromXml(Element node) {
    }

    public String getXmlContent() {
        return content;
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public void getNodeUsage(Set<Integer> list) {
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return true;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request, HttpServletResponse response)  throws Exception{
        return true;
    }

    /******************* HTML part *********************************/

    public void appendPartHtml(PageContext context, JspWriter writer, HttpServletRequest request, String sectionType, PageData pageData) throws IOException {
        if (pageData.isEditMode())
            appendEditPartHtml(context, writer, request, sectionType, pageData);
        else
            appendLivePartHtml(context, writer, request, pageData);
    }

    public void appendEditPartHtml(PageContext context, JspWriter writer, HttpServletRequest request, String sectionType, PageData pageData) throws IOException {
        Locale locale = SessionReader.getSessionLocale(request);
        writeEditPartStart(context, writer, request, pageData.getEditPagePart(), getSectionName(), pageData.getId(), locale);
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            partTemplate.writeTemplate(context, writer, request, pageData, this);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        writeEditPartEnd(context, writer, request, pageData.getEditPagePart(), getSectionName(), sectionType, pageData.getId(), locale);
    }

    public void appendLivePartHtml(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData) throws IOException {
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            writer.write("<div class=\"pagePart\" id=\"" + getHtmlId() + "\" >");
            partTemplate.writeTemplate(context, writer, request, pageData, this);
            writer.write("</div>");
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
    }

    protected void writeEditPartStart(PageContext context, JspWriter writer, HttpServletRequest request, PagePartData editPagePart, String sectionName, int pageId, Locale locale) throws IOException {
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div title=\"" + StringUtil.toHtml(getTemplateName()) + "(ID=" + getId() + ") - " + StringUtil.getHtml("_rightClickEditHint") + "\" id=\"part_" + getId() + "\" class = \"pagePart viewPagePart contextSource\">\n");
        } else if (this == editPagePart) {
            // this one currently edited
            writer.write("<div id=\"part_"+getId()+"\" class = \"pagePart editPagePart\">\n" +
                    "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">\n" +
                    "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>\n" +
                    "<input type = \"hidden\" name = \"pageId\" value = \""+pageId+"\"/>\n" +
                    "<input type = \"hidden\" name = \"sectionName\" value = \""+sectionName+"\"/>\n" +
                    "<input type = \"hidden\" name = \"partId\" value = \""+getId()+"\"/>\n" +
                    "<div class = \"buttonset editSection\">\n" +
                    "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">"+getHtml("_ok", locale)+"</button>\n" +
                    "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'"+pageId+"'});\">"+getHtml("_cancel", locale)+"</button>\n" +
                    "</div>");
        } else {
            // some other currently edited
            writer.write("<div class = \"pagePart viewPagePart\">\n");
        }
    }

    public void writeEditPartEnd(PageContext context, JspWriter writer, HttpServletRequest request, PagePartData editPagePart, String sectionName, String sectionType, int pageId, Locale locale) throws IOException {
        boolean staticSection = sectionType.equals(SectionData.TYPE_STATIC);
        // end of pagePart div of any kind
        writer.write("</div>");
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div class = \"contextMenu\">");
            if (isEditable()) {
                writer.write("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'" + pageId + "',sectionName:'" + sectionName + "',partId:'" + getId() + "'})\">" + getHtml("_edit", locale) + "</div>\n");
            }
            if (!staticSection) {
                appendContextCode(writer, sectionType, locale);
            }
            writer.write("</div>\n");
        } else if (this == editPagePart) {
            // this one currently edited
            writer.write("</form>\n");
        }
    }

    protected void appendContextCode(JspWriter writer, String sectionType, Locale locale) throws IOException {
        writer.write("<div class=\"icn isetting\" onclick = \"return openLayerDialog('"+getHtml("_settings", locale)+"', '/pagepart.ajx?act=openEditHtmlPartSettings&pageId="+pageId+"&sectionName="+sectionName+"&partId="+getId()+"');\">"+getHtml("_settings", locale)+"</div>\n" +
                "<div class=\"icn inew\" onclick = \"return openLayerDialog('"+getHtml("_addPart", locale)+"', '/pagepart.ajx?act=openAddPagePart&pageId="+pageId+"&sectionName="+sectionName+"&sectionType="+sectionType+"&partId="+getId()+"');\">"+getHtml("_newAbove", locale)+"</div>\n" +
                "<div class=\"icn inew\" onclick = \"return openLayerDialog('"+getHtml("_addPart", locale)+"', '/pagepart.ajx?act=openAddPagePart&pageId="+pageId+"&sectionName="+sectionName+"&sectionType="+sectionType+"&partId="+getId()+"&below=true');\">"+getHtml("_newBelow", locale)+"</div>\n" +
                "<div class=\"icn ishare\" onclick = \"return openLayerDialog('"+getHtml("_share", locale)+"', '/pagepart.ajx?act=openSharePagePart&pageId="+pageId+"&sectionName="+sectionName+"&partId="+getId()+"');\">"+getHtml("_share", locale)+"</div>\n" +
                "<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId="+pageId+"&sectionName="+sectionName+"&partId="+getId()+"&dir=-1');\">"+getHtml("_up", locale)+"</div>\n" +
                "<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId="+pageId+"&sectionName="+sectionName+"&partId="+getId()+"&dir=1');\">"+getHtml("_down", locale)+"</div>\n" +
                "<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.srv?',{act:'deletePagePart',pageId:'"+pageId+"',sectionName:'"+sectionName+"',partId:'"+getId()+"'});\">"+getHtml("_delete", locale)+"</div>\n");
    }

    public String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

}
