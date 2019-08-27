/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.jsppage.JspPageData;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.request.ForwardActionResult;
import de.elbe5.request.IActionResult;
import de.elbe5.request.PageActionResult;
import de.elbe5.request.RequestData;

public class SearchController extends Controller {

    public static final String KEY = "search";

    private static SearchController instance = new SearchController();

    public static SearchController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openSearch(RequestData rdata) {
        PageSearchResultData contentResult = new PageSearchResultData();
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IActionResult openUserSearch(RequestData rdata) {
        return showUserSearch();
    }

    public IActionResult search(RequestData rdata) {
        PageSearchResultData contentResult = new PageSearchResultData();
        String pattern = rdata.getString("searchPattern");
        contentResult.setPattern(pattern);
        SearchBean.getInstance().searchPages(contentResult);
        for (int i = contentResult.results.size() - 1; i >= 0; i--) {
            PageSearchData result = contentResult.results.get(i);
            if (!result.isAnonymous() && !rdata.hasContentRight(result.getId(), Right.READ))
                contentResult.results.remove(i);
        }
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IActionResult searchUsers(RequestData rdata) {
        UserSearchResultData userResult = new UserSearchResultData();
        userResult.setPattern(rdata.getString("searchPattern"));
        SearchBean.getInstance().searchUsers(userResult);
        rdata.put("searchResultData", userResult);
        return showSearch();
    }

    public IActionResult indexAllContent(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_PAGES);
        rdata.setMessage(Strings.string("_indexingContentQueued",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
    }

    public IActionResult indexAllUsers(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_USERS);
        rdata.setMessage(Strings.string("_indexingUsersQueued",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
    }

    protected IActionResult showSearch() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/search/search.jsp");
        return new PageActionResult(pageData);
    }

    protected IActionResult showUserSearch() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/search/userSearch.jsp");
        return new PageActionResult(pageData);
    }

}
