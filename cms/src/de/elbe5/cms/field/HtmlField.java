/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.webserver.tree.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public class HtmlField extends BaseField {
    public static String FIELDTYPE_HTML = "html";

    public static void initialize() {
        BaseField.baseFieldClasses.put(FIELDTYPE_HTML, HtmlField.class);
    }

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

    public String getHtmlForOutout() {
        return replacePageLinks(html);
    }

    @Override
    public Element generateXml(Document doc, Element parent) {
        Element elem = super.generateXml(doc, parent);
        XmlUtil.addCDATA(doc, elem, html);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        html = XmlUtil.getCData(node);
    }

    @Override
    public void getFileUsage(Set<Integer> list) {
        int start;
        int end = 0;
        String fileStr = "/file.srv?method=show&amp;fileId=";
        while (true) {
            start = html.indexOf(fileStr, end);
            if (start == -1) break;
            start += fileStr.length();
            end = html.indexOf('\"', start);
            if (end == -1) break;
            try {
                int fid = Integer.parseInt(html.substring(start, end));
                list.add(fid);
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    @Override
    public void getPageUsage(Set<Integer> list) {
        int start;
        int end = 0;
        String docStr = "/page.srv?method=show&amp;pageId=";
        while (true) {
            start = html.indexOf(docStr, end);
            if (start == -1) break;
            start += docStr.length();
            end = html.indexOf('\"', start);
            if (end == -1) break;
            try {
                int pid = Integer.parseInt(html.substring(start, end));
                list.add(pid);
            } catch (Exception ignored) {
            }
            end++;
        }
        docStr = ".html";
        end = 0;
        while (true) {
            start = html.indexOf(docStr, end);
            if (start == -1) break;
            start += docStr.length();
            end = html.indexOf(".html", start);
            if (end == -1) break;
            try {
                int pid = Integer.parseInt(html.substring(start, end));
                list.add(pid);
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    protected String replacePageLinks(String html) {
        String newHtml = html;
        int start;
        int end = 0;
        String docStr = "/page.srv?method=show&amp;pageId=";
        CmsTreeCache tc = CmsTreeCache.getInstance();
        while (true) {
            start = newHtml.indexOf(docStr, end);
            if (start == -1) break;
            start += docStr.length();
            end = newHtml.indexOf('\"', start);
            if (end == -1) break;
            try {
                int pid = Integer.parseInt(newHtml.substring(start, end));
                TreeNode page = tc.getNode(pid);
                String link = page.getUrl();
                int oldLength = end - start;
                newHtml = newHtml.substring(0, start) + link + newHtml.substring(end);
                end += link.length() - oldLength;
            } catch (Exception ignored) {
            }
            end++;
        }
        docStr = ".html";
        end = 0;
        while (true) {
            start = newHtml.indexOf(docStr, end);
            if (start == -1) break;
            start += docStr.length();
            end = newHtml.indexOf(".html", start);
            if (end == -1) break;
            try {
                int pid = Integer.parseInt(newHtml.substring(start, end));
                TreeNode page = tc.getNode(pid);
                String link = page.getUrl();
                int oldLength = end - start;
                newHtml = newHtml.substring(0, start) + link + newHtml.substring(end);
                end += link.length() - oldLength;
            } catch (Exception ignored) {
            }
            end++;
        }
        return newHtml;
    }

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setHtml(RequestHelper.getString(request, getIdentifier()));
        return isComplete();
    }
}
