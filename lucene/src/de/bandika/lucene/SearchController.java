/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import de.bandika._base.MasterResponse;
import de.bandika._base.Response;
import de.bandika._base.JspResponse;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika._base.SessionData;
import de.bandika._base.RequestData;
import de.bandika._base.*;

public class SearchController extends Controller {

  public static final String LINKKEY_SEARCH = "link|luceneSearch";

  private static SearchController instance = null;

  public static SearchController getInstance() {
    if (instance == null) {
      instance = new SearchController();
    }
    return instance;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (method.equals("openSearch")) return openSearch();
    if (method.equals("search")) return search(rdata);
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (sdata.hasBackendLinkRight(LINKKEY_SEARCH)) {
      if (method.equals("openSearchAdministration")) return openSearchAdministration();
      if (method.equals("indexAll")) return indexAll(rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showSearch() {
    return new JspResponse("/_jsp/lucene/search.jsp", StringCache.getString("luceneSearch"), MasterResponse.TYPE_USER);
  }

  protected Response showEdit() {
    return new JspResponse("/_jsp/lucene/editSearch.jsp", StringCache.getString("luceneSearch"), MasterResponse.TYPE_ADMIN);
  }

  public Response openSearch() {
    return showSearch();
  }

  public Response search(RequestData rdata) {
    SearchResultData result = new SearchResultData();
    result.setPattern(rdata.getParamString("pattern"));
    SearchBean.getInstance().search(result);
    rdata.setParam("searchResultData", result);
    return showSearch();
  }

  public Response openSearchAdministration() {
    return showEdit();
  }

  public Response indexAll(RequestData rdata) {
    SearchQueue.getInstance().addAction(new SearchActionData(SearchActionData.ACTION_INDEX_ALL, 0, null));
    rdata.setMessageKey("indexingQueued");
    return showEdit();
  }

}

