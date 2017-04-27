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

    public String getContentHtml(PageData pageData, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        appendEditPartHtml(sb, "", pageData, request);
        return sb.toString();
    }

    public void appendPartHtml(StringBuilder sb, String sectionType, PageData pageData, HttpServletRequest request) {
        if (pageData.isEditMode())
            appendEditPartHtml(sb, sectionType, pageData, request);
        else
            appendLivePartHtml(sb, pageData, request);
    }

    public void appendEditPartHtml(StringBuilder sb, String typeName, PageData pageData, HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        writeEditPartStart(pageData.getEditPagePart(), getSection(), pageData.getId(), sb, locale);
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            partTemplate.fillTemplate(sb, pageData, this, request);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        writeEditPartEnd(pageData.getEditPagePart(), getSection(), typeName, pageData.getId(), sb, locale);
    }

    public void appendLivePartHtml(StringBuilder sb, PageData pageData, HttpServletRequest request) {
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PART, getTemplateName());
        try {
            sb.append("<div class=\"pagePart\" id=\"").append(getHtmlId()).append("\" >");
            partTemplate.fillTemplate(sb, pageData, this, request);
            sb.append("</div>");
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
    }

    protected static String EDITSTARTCODE = "<div id=\"part_%s\" class = \"pagePart editPagePart\">\n" + "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">" + "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>" + "<input type = \"hidden\" name = \"pageId\" value = \"%s\"/>" + "<input type = \"hidden\" name = \"sectionName\" value = \"%s\"/>" + "<input type = \"hidden\" name = \"partId\" value = \"%s\"/>\n" + "<div class = \"buttonset editSection\">" + "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">%s</button>" + "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'%s'});\">%s</button>" + "</div>";

    protected void writeEditPartStart(PagePartData editPagePart, String sectionName, int pageId, StringBuilder sb, Locale locale) {
        if (editPagePart == null) {
            sb.append("<div title=\"").append(StringUtil.toHtml(getTemplateName())).append("(ID=").append(getId()).append(") - ").append(StringUtil.getHtml("_rightClickEditHint")).append("\" id=\"part_").append(getId()).append("\" class = \"pagePart viewPagePart contextSource\">\n");
        } else if (this == editPagePart) {
            sb.append(String.format(EDITSTARTCODE, getId(), pageId, sectionName, getId(), getHtml("_ok", locale), pageId, getHtml("_cancel", locale)));
        } else {
            sb.append("<div class = \"pagePart viewPagePart\">\n");
        }
    }

    private static String HTMLCONTEXTCODE = "<div class=\"icn isetting\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openEditHtmlPartSettings&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn inew\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openAddPagePart&pageId=%s&sectionName=%s&sectionType=%s&partId=%s&below=true');\">%s</div>\n" +
            "<div class=\"icn ishare\" onclick = \"return openLayerDialog('%s', '/pagepart.ajx?act=openSharePagePart&pageId=%s&sectionName=%s&partId=%s');\">%s</div>\n" +
            "<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=-1');\">%s</div>\n" +
            "<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.srv?act=movePagePart&pageId=%s&sectionName=%s&partId=%s&dir=1');\">%s</div>\n" +
            "<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.srv?',{act:'deletePagePart',pageId:'%s',sectionName:'%s',partId:'%s'});\">%s</div>\n";

    public void writeEditPartEnd(PagePartData editPagePart, String sectionName, String sectionType, int pageId, StringBuilder sb, Locale locale) {
        boolean staticSection = sectionName.equals(PageData.STATIC_SECTION_NAME);
        sb.append("</div>");
        if (editPagePart == null) {
            sb.append("<div class = \"contextMenu\">");
            sb.append("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'").append(pageId).append("',sectionName:'").append(sectionName).append("',partId:'").append(getId()).append("'})\">").append(getHtml("_edit", locale)).append("</div>\n");
            if (!staticSection) {
                sb.append(String.format(HTMLCONTEXTCODE, getHtml("_settings", locale), pageId, sectionName, getId(), getHtml("_settings", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newAbove", locale),
                        getHtml("_addPart", locale), pageId, sectionName, sectionType, getId(), getHtml("_newBelow", locale),
                        getHtml("_share", locale), pageId, sectionName, getId(), getHtml("_share", locale),
                        pageId, sectionName, getId(), getHtml("_up", locale),
                        pageId, sectionName, getId(), getHtml("_down", locale),
                        pageId, sectionName, getId(), getHtml("_delete", locale)));
            }
            sb.append("</div>\n");
        } else if (this == editPagePart) {
            sb.append("</form>\n");
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
