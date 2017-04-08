/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.application.AdminAction;
import de.bandika.application.MasterStatics;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.ICmsAction;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.RequestWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum SearchAction implements ICmsAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return SearchAction.openSearch.execute(request, response);
        }
    }, /**
     * opens search page
     */
    openSearch {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showSearch(request, response);
                }
            }, /**
     * executes a search and shows the results
     */
    search {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    ContentSearchResultData contentResult = new ContentSearchResultData();
                    String pattern=RequestReader.getString(request, "searchPattern");
                    contentResult.setPattern(pattern);
                    SearchBean.getInstance().searchContent(contentResult);
                    request.setAttribute("contentSearchResultData", contentResult);
                    UserSearchResultData userResult = new UserSearchResultData();
                    userResult.setPattern(pattern);
                    SearchBean.getInstance().searchUsers(userResult);
                    request.setAttribute("userSearchResultData", userResult);
                    return showSearch(request, response);
                }
            }, /**
     * shows search settings and properties
     */
    showAdminSearchDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                        return false;
                    return showAdminSearchDetails(request, response);
                }
            },/**
     * shows site search result details
     */
    showSiteSearchDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showSiteSearchDetails(request, response);
                }
            },/**
     * shows page search result details
     */
    showPageSearchDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showPageSearchDetails(request, response);
                }
            },/**
     * shows file search result details
     */
    showFileSearchDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showFileSearchDetails(request, response);
                }
            },/**
     * shows user search result details
     */
    showUserSearchDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showUserSearchDetails(request, response);
                }
            }, /**
     * reindexes all content elements
     */
    indexAllContent {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                        return false;
                    SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_CONTENT, 0, null));
                    RequestWriter.setMessageKey(request, "_indexingContentQueued");
                    return AdminAction.openAdministration.execute(request, response);
                }
            },
    /**
     * reindexes all users
     */
    indexAllUsers {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_USERS, 0, null));
            RequestWriter.setMessageKey(request, "_indexingUsersQueued");
            return AdminAction.openAdministration.execute(request, response);
        }
    };

    public static final String KEY = "search";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, SearchAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showSearch(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/search/search.jsp", MasterStatics.PAGE_MASTER);
    }

    protected boolean showAdminSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/searchDetails.ajax.jsp");
    }

    protected boolean showSiteSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/siteSearchDetails.ajax.jsp");
    }

    protected boolean showPageSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/pageSearchDetails.ajax.jsp");
    }

    protected boolean showFileSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/fileSearchDetails.ajax.jsp");
    }

    protected boolean showUserSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/userSearchDetails.ajax.jsp");
    }

}
