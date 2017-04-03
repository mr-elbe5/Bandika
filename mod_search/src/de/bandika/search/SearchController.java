/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.data.IRights;
import de.bandika.data.StringCache;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

public class SearchController extends Controller {

    public static final int LINKID_SEARCH = 111;

    private static SearchController instance = null;

    public static void setInstance(SearchController instance) {
        SearchController.instance = instance;
    }

    public static SearchController getInstance() {
        if (instance == null) {
            instance = new SearchController();
        }
        return instance;
    }

    public String getKey(){
        return "lucenesearch";
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata)
            throws Exception {
        if (action.equals("openSearch")) return openSearch(sdata);
        if (action.equals("search")) return search(rdata,sdata);
        if (!sdata.isLoggedIn())
            return UserController.getInstance().openLogin();
        if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, IRights.ID_GENERAL, PageRightsData.RIGHT_EDIT)) {
            if (action.equals("openSearchAdministration")) return openSearchAdministration(sdata);
            if (action.equals("indexAll")) return indexAll(rdata, sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected Response showSearch(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/search/search.jsp", StringCache.getString("lucene_search", sdata.getLocale()), MasterResponse.TYPE_USER);
    }

    protected Response showEdit(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/search/editSearch.jsp", StringCache.getString("lucene_search", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openSearch(SessionData sdata) {
        return showSearch(sdata);
    }

    public Response search(RequestData rdata, SessionData sdata) {
        SearchResultData result = new SearchResultData();
        result.setPattern(rdata.getString("pattern"));
        SearchBean.getInstance().search(result);
        rdata.put("searchResultData", result);
        return showSearch(sdata);
    }

    public Response openSearchAdministration(SessionData sdata) {
        return showEdit(sdata);
    }

    public Response indexAll(RequestData rdata, SessionData sdata) {
        SearchQueue.getInstance().addAction(new SearchActionData(SearchActionData.ACTION_INDEX_ALL, 0, null));
        rdata.setMessageKey("lucene_indexingQueued", sdata.getLocale());
        return showEdit(sdata);
    }

}

