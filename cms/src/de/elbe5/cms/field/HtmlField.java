/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.search.SearchHelper;
import de.elbe5.cms.servlet.RequestData;

import java.util.Set;

public class HtmlField extends StaticField {

    public static final String FIELDTYPE = "html";

    public static final String SRC_PATTERN = " src=\"/file/show/";
    public static final String PAGE_LINK_PATTERN = " href=\"/page/show/";
    public static final String FILE_LINK_PATTERN = " href=\"/file/show/";

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
    public void readRequestData(RequestData rdata) {
        setContent(rdata.getString(getIdentifier()));
    }

    public static void registerPagesInHtml(String html, Set<Integer> list) {
        registerIdsInHtml(html,PAGE_LINK_PATTERN,list);
    }

    public static void registerFilesInHtml(String html, Set<Integer> list) {
        registerIdsInHtml(html,FILE_LINK_PATTERN,list);
    }

    public static void registerImagesInHtml(String html, Set<Integer> list) {
        registerIdsInHtml(html,SRC_PATTERN,list);
    }

    public static void registerIdsInHtml(String html, String pattern, Set<Integer> list) {
        int start;
        int end = 0;
        while (true) {
            start = html.indexOf(pattern, end);
            if (start == -1) {
                break;
            }
            // keep '/'
            start += pattern.length() - 1;
            end = html.indexOf('\"', start);
            if (end == -1) {
                break;
            }
            try {
                int id = Integer.parseInt(html.substring(start, end));
                list.add(id);
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
