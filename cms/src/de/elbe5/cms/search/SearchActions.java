/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.search;

import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.page.JspPageData;
import de.elbe5.cms.servlet.ActionSet;
import de.elbe5.cms.servlet.ActionSetCache;
import de.elbe5.cms.servlet.RequestReader;
import de.elbe5.cms.servlet.SuccessMessage;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchActions extends ActionSet {

    public static final String openSearch="openSearch";
    public static final String openUserSearch="openUserSearch";
    public static final String search="search";
    public static final String searchUsers="searchUsers";
    public static final String indexAllContent="indexAllContent";
    public static final String indexAllUsers="indexAllUsers";

    public static final String KEY = "search";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new SearchActions());
    }

    private SearchActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case openSearch: {
                return showSearch(request, response);
            }
            case openUserSearch: {
                return showUserSearch(request, response);
            }
            case search: {
                PageSearchResultData contentResult = new PageSearchResultData();
                String pattern = RequestReader.getString(request, "searchPattern");
                contentResult.setPattern(pattern);
                SearchBean.getInstance().searchPages(contentResult);
                request.setAttribute("searchResultData", contentResult);
                return showSearch(request, response);
            }
            case searchUsers: {
                UserSearchResultData userResult = new UserSearchResultData();
                userResult.setPattern(RequestReader.getString(request, "searchPattern"));
                SearchBean.getInstance().searchUsers(userResult);
                request.setAttribute("searchResultData", userResult);
                return showSearch(request, response);
            }
            case indexAllContent: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_PAGES);
                SuccessMessage.setMessageByKey(request, Strings._indexingContentQueued);
                return sendForwardResponse(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration);
            }
            case indexAllUsers: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_USERS);
                SuccessMessage.setMessageByKey(request, Strings._indexingUsersQueued);
                return sendForwardResponse(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration);
            }
            default: {
                return showSearch(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showSearch(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/search/search.jsp");
        return setPageResponse(request, response, pageData);
    }

    protected boolean showUserSearch(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/search/userSearch.jsp");
        return setPageResponse(request, response, pageData);
    }

}
