/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.team;

import de.bandika.cms.servlet.CmsActions;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamBlogActions extends CmsActions {

    public static final String openCreateEntry="openCreateEntry";
    public static final String openEditEntry="openEditEntry";
    public static final String saveEntry="saveEntry";
    public static final String openDeleteEntry="openDeleteEntry";
    public static final String deleteEntry="deleteEntry";

    public final static int MODE_LIST = 0;
    public final static int MODE_EDIT = 1;
    public final static int MODE_DELETE = 2;

    public static final String KEY = "teamblog";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TeamBlogActions());
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openCreateEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamBlogEntryData data = new TeamBlogEntryData();
                data.setTeamPartId(RequestReader.getInt(request, "pid"));
                data.setId(TeamBlogBean.getInstance().getNextId());
                data.setNew(true);
                request.setAttribute("entry", data);
                request.setAttribute("viewMode", Integer.toString(MODE_EDIT));
                return showPage(request, response, data, RequestReader.getInt(request,"id"));
            }
            case openEditEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                int id = RequestReader.getInt(request,"eid");
                TeamBlogEntryData data = TeamBlogBean.getInstance().getEntryData(id);
                SessionWriter.setSessionObject(request, "entry", data);
                request.setAttribute("viewMode", Integer.toString(MODE_EDIT));
                return showPage(request, response, data, RequestReader.getInt(request,"id"));
            }
            case saveEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamBlogEntryData data = (TeamBlogEntryData) SessionReader.getSessionObject(request, "entry");
                if (data == null || data.getId() != RequestReader.getInt(request,"eid"))
                    return false;
                if (!data.readRequestData(request)) {
                    request.setAttribute("viewMode", Integer.toString(MODE_EDIT));
                    return showPage(request, response, data, RequestReader.getInt(request,"id"));
                }
                data.prepareSave();
                TeamBlogBean.getInstance().saveEntryData(data);
                request.setAttribute("viewMode", Integer.toString(MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request,"id"));
            }
            case openDeleteEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                request.setAttribute("viewMode", Integer.toString(MODE_DELETE));
                return showPage(request, response, null, RequestReader.getInt(request,"id"));
            }
            case deleteEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamBlogBean.getInstance().deleteEntry(RequestReader.getInt(request,"eid"));
                request.setAttribute("viewMode", Integer.toString(MODE_LIST));
                return showPage(request, response, null, RequestReader.getInt(request,"id"));
            }
        }
        return false;
    }

    public boolean showImportTemplates(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    protected boolean showPage(HttpServletRequest request, HttpServletResponse response,TeamBlogEntryData data, int pageId) {
        if (data!=null)
            request.setAttribute("entryData", data);
        request.setAttribute("pageData", TreeCache.getInstance().getPage(pageId));
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }
}