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
import de.bandika.page.PagePartData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Set;

public class HtmlPartData extends PagePartData {

    protected String html = "";

    public String getHtml() {
        return html;
    }

    public String getHtmlForOutput() {
        return replacePageLinks(html);
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public void generateContentXml(Document doc, Element root) {
        Element elem = XmlHelper.createChild(doc, root, "html");
        XmlHelper.createCDATA(doc, elem, html);
    }

    @Override
    public void evaluateContentXml(Element root) {
        NodeList fieldNodes = XmlHelper.getChildNodes(root, "html");
        if (fieldNodes.getLength() > 0) {
            Element child = (Element) fieldNodes.item(0);
            html = XmlHelper.getCData(child);
        }
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
    }

    public boolean isComplete(SessionData sdata) {
        return true;
    }

    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        super.readPagePartRequestData(rdata, sdata);
        setHtml(rdata.getString("htmlArea"));
        return isComplete(sdata);
    }

    protected String replacePageLinks(String html) {
        String html1 = html;
        int start;
        int end = 0;
        String docStr = "/page.srv?method=show&amp;id=";
        MenuCache mc = MenuCache.getInstance();
        while (true) {
            start = html1.indexOf(docStr, end);
            if (start == -1)
                break;
            start += docStr.length();
            end = html1.indexOf('\"', start);
            if (end == -1)
                break;
            try {
                int pid = Integer.parseInt(html1.substring(start, end));
                MenuData page = mc.getNode(pid);
                String link = page.getUrl();
                int oldLength = end - start;
                html1 = html1.substring(0, start) + link + html1.substring(end);
                end += link.length() - oldLength;
            } catch (Exception ignored) {
            }
            end++;
        }
        return html1;
    }

}
