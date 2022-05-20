/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.*;
import de.elbe5.rights.SystemZone;
import de.elbe5.search.html.SearchPage;
import de.elbe5.search.html.UserSearchPage;
import de.elbe5.servlet.Controller;
import de.elbe5.request.RequestData;
import de.elbe5.servlet.ControllerCache;

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

    public IResponse openSearch(RequestData rdata) {
        ContentSearchResultData contentResult = new ContentSearchResultData();
        rdata.getAttributes().put("searchResultData", contentResult);
        return showSearch();
    }

    public IResponse openUserSearch(RequestData rdata) {
        return showUserSearch();
    }

    public IResponse search(RequestData rdata) {
        ContentSearchResultData contentResult = new ContentSearchResultData();
        String pattern = rdata.getAttributes().getString("searchPattern");
        contentResult.setPattern(pattern);
        SearchBean.getInstance().searchPages(contentResult);
        for (int i = contentResult.results.size() - 1; i >= 0; i--) {
            ContentSearchData result = contentResult.results.get(i);
            if (!result.hasOpenAccess() && !result.hasReadRight(rdata))
                contentResult.results.remove(i);
        }
        rdata.getAttributes().put("searchResultData", contentResult);
        return showSearch();
    }

    public IResponse searchUsers(RequestData rdata) {
        UserSearchResultData userResult = new UserSearchResultData();
        userResult.setPattern(rdata.getAttributes().getString("searchPattern"));
        SearchBean.getInstance().searchUsers(userResult);
        rdata.getAttributes().put("searchResultData", userResult);
        return showSearch();
    }

    public IResponse indexAllContent(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_PAGES);
        rdata.setMessage(Strings.getString("_indexingContentQueued"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new ForwardResponse("/ctrl/admin/openSystemAdministration");
    }

    public IResponse indexAllUsers(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_USERS);
        rdata.setMessage(Strings.getString("_indexingUsersQueued"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new ForwardResponse("/ctrl/admin/openSystemAdministration");
    }

    protected IResponse showSearch() {
        return new MasterResponse(new SearchPage());
    }

    protected IResponse showUserSearch() {
        return new MasterResponse(new UserSearchPage());
    }

}
