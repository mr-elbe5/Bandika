/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.base.cache.Strings;
import de.elbe5.content.JspContentData;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.IView;
import de.elbe5.view.ContentView;
import de.elbe5.view.UrlView;

public class SearchController extends Controller {

    public static final String KEY = "search";

    private static SearchController instance = null;

    public static void setInstance(SearchController instance) {
        SearchController.instance = instance;
    }

    public static SearchController getInstance() {
        return instance;
    }

    public static void register(SearchController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openSearch(SessionRequestData rdata) {
        PageSearchResultData contentResult = new PageSearchResultData();
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IView openUserSearch(SessionRequestData rdata) {
        return showUserSearch();
    }

    public IView search(SessionRequestData rdata) {
        PageSearchResultData contentResult = new PageSearchResultData();
        String pattern = rdata.getString("searchPattern");
        contentResult.setPattern(pattern);
        SearchBean.getInstance().searchPages(contentResult);
        for (int i = contentResult.results.size() - 1; i >= 0; i--) {
            PageSearchData result = contentResult.results.get(i);
            //todo
            //if (!result.hasOpenAccess() && !rdata.hasContentReadRight(result.getId()))
            //    contentResult.results.remove(i);
        }
        rdata.put("searchResultData", contentResult);
        return showSearch();
    }

    public IView searchUsers(SessionRequestData rdata) {
        UserSearchResultData userResult = new UserSearchResultData();
        userResult.setPattern(rdata.getString("searchPattern"));
        SearchBean.getInstance().searchUsers(userResult);
        rdata.put("searchResultData", userResult);
        return showSearch();
    }

    public IView indexAllContent(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_PAGES);
        rdata.setMessage(Strings.string("_indexingContentQueued",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new UrlView("/ctrl/admin/openSystemAdministration");
    }

    public IView indexAllUsers(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_USERS);
        rdata.setMessage(Strings.string("_indexingUsersQueued",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new UrlView("/ctrl/admin/openSystemAdministration");
    }

    protected IView showSearch() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/search/search.jsp");
        return new ContentView(contentData);
    }

    protected IView showUserSearch() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/search/userSearch.jsp");
        return new ContentView(contentData);
    }

}
