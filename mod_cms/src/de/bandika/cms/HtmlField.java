/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika.data.XmlHelper;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.search.SearchHelper;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        XmlHelper.createCDATA(doc, elem, html);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        html = XmlHelper.getCData(node);
    }

    @Override
    public void addSearchContent(StringBuffer buffer) {
        String searchContent = SearchHelper.getSearchContentFromHtml(html);
        buffer.append(' ').append(searchContent).append(' ');
    }

    @Override
    public void getDocumentUsage(Set<Integer> list) {
        int start;
        int end = 0;
        String fileStr = "/document.srv?method=show&amp;fid=";
        while (true) {
            start = html.indexOf(fileStr, end);
            if (start == -1)
                break;
            start += fileStr.length();
            end = html.indexOf('\"', start);
            if (end == -1)
                break;
            try {
                int fid = Integer.parseInt(html.substring(start, end));
                list.add(fid);
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    @Override
    public void getImageUsage(Set<Integer> list) {
        int start;
        int end = 0;
        String fileStr = "/image.srv?method=show&amp;fid=";
        while (true) {
            start = html.indexOf(fileStr, end);
            if (start == -1)
                break;
            start += fileStr.length();
            end = html.indexOf('\"', start);
            if (end == -1)
                break;
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
        String docStr = "/page.srv?method=show&amp;id=";
        while (true) {
            start = html.indexOf(docStr, end);
            if (start == -1)
                break;
            start += docStr.length();
            end = html.indexOf('\"', start);
            if (end == -1)
                break;
            try {
                int pid = Integer.parseInt(html.substring(start, end));
                list.add(pid);
            } catch (Exception ignored) {
            }
            end++;
        }
        docStr = ".pg";
        end = 0;
        while (true) {
            start = html.indexOf(docStr, end);
            if (start == -1)
                break;
            start += docStr.length();
            end = html.indexOf(".page", start);
            if (end == -1)
                break;
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
        String docStr = "/_page?method=show&amp;id=";
        MenuCache mc = MenuCache.getInstance();
        while (true) {
            start = newHtml.indexOf(docStr, end);
            if (start == -1)
                break;
            start += docStr.length();
            end = newHtml.indexOf('\"', start);
            if (end == -1)
                break;
            try {
                int pid = Integer.parseInt(newHtml.substring(start, end));
                MenuData page = mc.getNode(pid);
                String link = page.getUrl();
                int oldLength = end - start;
                newHtml = newHtml.substring(0, start) + link + newHtml.substring(end);
                end += link.length() - oldLength;
            } catch (Exception ignored) {
            }
            end++;
        }
        docStr = ".pg";
        end = 0;
        while (true) {
            start = newHtml.indexOf(docStr, end);
            if (start == -1)
                break;
            start += docStr.length();
            end = newHtml.indexOf(".page", start);
            if (end == -1)
                break;
            try {
                int pid = Integer.parseInt(newHtml.substring(start, end));
                MenuData page = mc.getNode(pid);
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
    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        setHtml(rdata.getString(getIdentifier()));
        return isComplete(sdata);
    }

}
