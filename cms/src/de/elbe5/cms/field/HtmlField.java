/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.file.FileCache;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageCache;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.search.SearchHelper;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public class HtmlField extends StaticField {

    public static final String FIELDTYPE = "html";

    public static final String SRC_PATTERN = " src=\"/";
    public static final String LINK_PATTERN = " href=\"/";

    @Override
    public String getFieldType() {
        return FIELDTYPE;
    }

    @Override
    public void getNodeUsage(Set<Integer> list) {
        registerImagesInHtml(content, list);
        registerPagesInHtml(content, list);
    }

    /******************* HTML part *********************************/

    @Override
    public void readRequestData(HttpServletRequest request, RequestError error) {
        setContent(RequestReader.getString(request, getIdentifier()));
    }

    public static void registerPagesInHtml(String html, Set<Integer> list) {
        int start;
        int end = 0;
        while (true) {
            start = html.indexOf(LINK_PATTERN, end);
            if (start == -1) {
                break;
            }
            // keep '/'
            start += LINK_PATTERN.length() - 1;
            end = html.indexOf('\"', start);
            if (end == -1) {
                break;
            }
            try {
                String url = html.substring(start, end);
                PageData page = PageCache.getInstance().getPage(url);
                if (page != null)
                    list.add(page.getId());
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    public static void registerImagesInHtml(String html, Set<Integer> list) {
        int start;
        int end = 0;
        while (true) {
            start = html.indexOf(SRC_PATTERN, end);
            if (start == -1) {
                break;
            }
            // keep '/'
            start += SRC_PATTERN.length() - 1;
            end = html.indexOf('\"', start);
            if (end == -1) {
                break;
            }
            try {
                String url = html.substring(start, end);
                FileData file = FileCache.getInstance().getFile(url);
                if (file != null)
                    list.add(file.getId());
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        sb.append(" ").append(SearchHelper.getSearchContentFromHtml(getContent()));
    }

}
