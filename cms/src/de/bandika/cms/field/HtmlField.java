/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.search.HtmlStripper;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.template.TemplateAttributes;
import de.bandika.cms.tree.TreeNode;
import de.bandika.servlet.RequestReader;
import de.bandika.cms.tree.TreeCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

public class HtmlField extends Field {

    public static String FIELDTYPE_HTML = "html";

    @Override
    public String getFieldType() {
        return FIELDTYPE_HTML;
    }

    protected String html = "";

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public String getHtmlForOutput() {
        return html;
    }

    @Override
    public void getNodeUsage(Set<Integer> list) {
        registerNodesInHtml(html, " href=\"/", list);
        registerNodesInHtml(html, " src=\"/", list);
    }

    /******************* HTML part *********************************/

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setHtml(RequestReader.getString(request, getIdentifier()));
        return isComplete();
    }

    public static void registerNodesInHtml(String html, String linkPattern, Set<Integer> list) {
        int start;
        int end = 0;
        while (true) {
            start = html.indexOf(linkPattern, end);
            if (start == -1) {
                break;
            }
            // keep '/'
            start += linkPattern.length() - 1;
            end = html.indexOf('\"', start);
            if (end == -1) {
                break;
            }
            try {
                String url = html.substring(start, end);
                TreeNode node = TreeCache.getInstance().getNode(url);
                if (node != null)
                    list.add(node.getId());
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    private static String CKCODE = "" + "<div class=\"ckeditField\" id=\"%s\" contenteditable=\"true\">%s</div>" + "<input type=\"hidden\" name=\"%s\" value=\"%s\" />" + "<script type=\"text/javascript\">$('#%s').ckeditor({" + "toolbar : '%s'," + "filebrowserBrowseUrl : '/field.srv?act=openLinkBrowser&siteId=%s&pageId=%s'," + "filebrowserImageBrowseUrl : '/field.srv?act=openImageBrowser&siteId=%s&pageId=%s'" + "});" + "</script>";

    @Override
    public void appendFieldHtml(StringBuilder sb, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData, HttpServletRequest request) {
        String toolbar = attributes.getString("toolbar");
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        int siteId = pageData.getParentId();
        int pageId = pageData.getId();
        String html = getHtml().trim();
        if (partEditMode) {
            sb.append(String.format(CKCODE, getIdentifier(), html.isEmpty() ? defaultContent : html, getIdentifier(), StringUtil.toHtml(html), getIdentifier(), toolbar, siteId, pageId, siteId, pageId));
        } else {
            try {
                if (html.isEmpty()) {
                    sb.append("");
                } else {
                    sb.append(getHtmlForOutput());
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void appendFieldHtml(PageContext context, JspWriter writer, HttpServletRequest request, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData) throws IOException {
        String toolbar = attributes.getString("toolbar");
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        int siteId = pageData.getParentId();
        int pageId = pageData.getId();
        String html = getHtml().trim();
        if (partEditMode) {
            writer.write(String.format(CKCODE, getIdentifier(), html.isEmpty() ? defaultContent : html, getIdentifier(), StringUtil.toHtml(html), getIdentifier(), toolbar, siteId, pageId, siteId, pageId));
        } else {
            try {
                if (html.isEmpty()) {
                    writer.write("");
                } else {
                    writer.write(getHtmlForOutput());
                }
            } catch (Exception ignored) {
            }
        }
    }

    /******************* XML part *********************************/

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, html);
        return node;
    }

    @Override
    public void fromXml(Element node) {
        super.fromXml(node);
        html = XmlUtil.getCData(node);
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        try {
            sb.append(" ").append(HtmlStripper.stripHtml(html));
        } catch (ParseException ignore) {
        }
    }

}
