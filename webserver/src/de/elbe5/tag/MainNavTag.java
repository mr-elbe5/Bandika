/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.page.PageCache;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainNavTag extends BaseTag {

    private boolean plain = false;

    public void setPlain(boolean value) {
        this.plain = value;
    }

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            Locale locale = rdata.getSessionLocale();

            PageData rootPage = PageCache.getInstance().getHomePage(locale);
            PageData currentPage = rdata.getCurrentPage();
            if (currentPage==null)
                currentPage=rootPage;
            if (rootPage != null){
                if (plain) {
                    addPlainTopNodes(writer, rdata, rootPage, currentPage);
                } else {
                    Set<Integer> activeIds= new HashSet<>();
                    currentPage.collectParentIds(activeIds);
                    addTopNodes(writer, rdata, rootPage, activeIds);
                }
            }

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

    public void addPlainTopNodes(JspWriter writer, RequestData rdata, PageData parentPage, PageData currentPage) throws IOException {
        if (parentPage.isInTopNav() && parentPage.isVisibleToUser(rdata)) {
            StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link {1}\" href=\"{2}\">{3}</a></li>",
                    parentPage.getId()==currentPage.getId() ? "active" : "", parentPage.getUrl(), StringUtil.toHtml(parentPage.getDisplayName()));
        }
        for (PageData page : parentPage.getSubPages()) {
            if (page.isInTopNav() && page.isVisibleToUser(rdata)) {
                StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link {1}\" href=\"{2}\">{3}</a></li>",
                        page.getId()==currentPage.getId() ? "active" : "", page.getUrl(), StringUtil.toHtml(page.getDisplayName()));
            }
        }
    }

    public void addTopNodes(JspWriter writer, RequestData rdata, PageData parentPage, Set<Integer> activeIds) throws IOException {
        for (PageData page : parentPage.getSubPages()) {
            if (page.isInTopNav() && page.isVisibleToUser(rdata)) {
                boolean hasSubPages = page.getSubPages().size() > 1;
                if (hasSubPages) {
                    writer.write("<li class=\"nav-item dropdown\">");
                    StringUtil.write(writer, "<a class=\"nav-link {1} dropdown-toggle\" data-toggle=\"dropdown\" href=\"{2}\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">{3}</a>",
                            activeIds.contains(page.getId())? "active" : "", page.getUrl(), StringUtil.toHtml(page.getDisplayName()));
                    writer.write("<div class=\"dropdown-menu carbon\">");
                    StringUtil.write(writer, "<a class=\"dropdown-item\" href=\"{1}\">{2}</a>",
                            page.getUrl(), StringUtil.toHtml(page.getDisplayName()));
                    addSubPages(writer, rdata, page, activeIds);
                    writer.write("</div></li>");
                } else {
                    StringUtil.write(writer, "<li class=\"nav-item {1}\"><a class=\"nav-link\" href=\"{2}\">{3}</a></li>",
                            activeIds.contains(page.getId())? "active" : "",page.getUrl(), StringUtil.toHtml(page.getDisplayName()));
                }
            }
        }
    }

    public void addSubPages(JspWriter writer, RequestData rdata, PageData parentPage, Set<Integer> activeIds) throws IOException {
        for (PageData page : parentPage.getSubPages()) {
            if (page.isInTopNav() && page.isVisibleToUser(rdata)) {
                boolean hasSubPages = page.getSubPages().size() > 1;
                StringUtil.write(writer, "<a class=\"dropdown-item {1}\" href=\"{2}\">{3}</a>",
                        activeIds.contains(page.getId())? "active" : "", page.getUrl(), StringUtil.toHtml(page.getDisplayName()));
                if (hasSubPages) {
                    writer.write("");
                    addSubPages(writer, rdata, page, activeIds);
                    writer.write("");
                }
            }
        }
    }

}
