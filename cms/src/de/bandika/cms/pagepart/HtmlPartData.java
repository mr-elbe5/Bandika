/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.pagepart;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.page.PageData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.template.TemplateType;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.SessionReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HtmlPartData extends PagePartData {

    protected String cssClass = "";
    protected PagePartContent partContent = new PagePartContent();

    public HtmlPartData(){
    }

    public void cloneData(PagePartData data) {
        super.cloneData(data);
        if (data instanceof HtmlPartData) {
            HtmlPartData htmlData = (HtmlPartData)data;
            setCssClass(htmlData.getCssClass());
            copyContent(htmlData);
        }
    }

    public void copyContent(PagePartData part) {
        if (part instanceof HtmlPartData) {
            HtmlPartData htmlData = (HtmlPartData)part;
            partContent.copyContent(htmlData.getPartContent());
        }
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public PagePartContent getPartContent() {
        return partContent;
    }

    public Field ensureField(String name, String fieldType) {
        return getPartContent().ensureField(name, fieldType);
    }

    public void getNodeUsage(Set<Integer> list) {
        partContent.getNodeUsage(list);
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return getPartContent().readPagePartRequestData(request);
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        setCssClass(RequestReader.getString(request, "cssClass"));
    }

    /******************* HTML part *********************************/

    protected static String EDITSTARTCODE = "<div id=\"part_%s\" class = \"pagePart editPagePart\">\n" + "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">" + "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>" + "<input type = \"hidden\" name = \"pageId\" value = \"%s\"/>" + "<input type = \"hidden\" name = \"sectionName\" value = \"%s\"/>" + "<input type = \"hidden\" name = \"partId\" value = \"%s\"/>\n" + "<div class = \"buttonset editSection\">" + "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">%s</button>" + "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'%s'});\">%s</button>" + "</div>";

    private static String HTMLCONTEXTCODE = "<div class=\"icn isetting\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openEditHtmlPartSettings&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s&below=true');\">%s</div>\n" +
            "<div class=\"icn ishare\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openSharePagePart&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=-1');\">%s</div>\n" +
            "<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=1');\">%s</div>\n" +
            "<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.srv?',{act:'deletePagePart',pageId:'%s',sectionName:'%s',partId:'%s'});\">%s</div>\n";

    public void appendPartHtml(PageContext context, JspWriter writer, HttpServletRequest request, String sectionType, PageData pageData) throws IOException {
        if (pageData.isEditMode())
            appendEditPartHtml(context, writer, request, sectionType, pageData);
        else
            appendLivePartHtml(context, writer, request, pageData);
    }

    public void appendEditPartHtml(PageContext context, JspWriter writer, HttpServletRequest request, String typeName, PageData pageData) throws IOException {
        Locale locale = SessionReader.getSessionLocale(request);
        writeEditPartStart(context, writer, request, pageData.getEditPagePart(), getSection(), pageData.getId(), locale);
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            partTemplate.writeTemplate(context, writer, request, pageData, this);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        writeEditPartEnd(context, writer, request, pageData.getEditPagePart(), getSection(), typeName, pageData.getId(), locale);
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
            writer.write("<div title=\"" + StringUtil.toHtml(getTemplateName()) + "(ID=" + getId() + ") - " + StringUtil.getHtml("_rightClickEditHint") + "\" id=\"part_" + getId() + "\" class = \"pagePart viewPagePart contextSource\">\n");
        } else if (this == editPagePart) {
            writer.write(String.format(EDITSTARTCODE, getId(), pageId, sectionName, getId(), getHtml("_ok", locale), pageId, getHtml("_cancel", locale)));
        } else {
            writer.write("<div class = \"pagePart viewPagePart\">\n");
        }
    }

    public void writeEditPartEnd(PageContext context, JspWriter writer, HttpServletRequest request, PagePartData editPagePart, String sectionName, String sectionType, int pageId, Locale locale) throws IOException {
        boolean staticSection = sectionName.equals(PageData.STATIC_SECTION_NAME);
        writer.write("</div>");
        if (editPagePart == null) {
            writer.write("<div class = \"contextMenu\">");
            writer.write("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'" + pageId + "',sectionName:'" + sectionName + "',partId:'" + getId() + "'})\">" + getHtml("_edit", locale) + "</div>\n");
            if (!staticSection) {
                writer.write(String.format(HTMLCONTEXTCODE, getHtml("_settings", locale), pageId, sectionName, getId(), getHtml("_settings", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newAbove", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newBelow", locale),
                        getHtml("_share", locale), pageId, sectionName, getId(), getHtml("_share", locale),
                        pageId, sectionName, getId(), getHtml("_up", locale),
                        pageId, sectionName, getId(), getHtml("_down", locale),
                        pageId, sectionName, getId(), getHtml("_delete", locale)));
            }
            writer.write("</div>\n");
        } else if (this == editPagePart) {
            writer.write("</form>\n");
        }
    }

    /******************* XML part *********************************/

    public void getXmlAttributes(Element node) {
        super.getXmlAttributes(node);
        setCssClass(XmlUtil.getStringAttribute(node, "cssClass"));

    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
        super.addXmlAttributes(xmlDoc, node);
        XmlUtil.addAttribute(xmlDoc, node, "cssClass", StringUtil.toXml(getCssClass()));
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        addXmlAttributes(xmlDoc, node);
        partContent.toXml(xmlDoc, node);
        return node;
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        getXmlAttributes(node);
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                PagePartContent content = new PagePartContent();
                content.setPagePartId(getId());
                content.fromXml(child);
                partContent = content;
            }
        }
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        partContent.appendSearchText(sb);
    }

}
