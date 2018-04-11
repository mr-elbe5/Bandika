/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.blog;

import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BlogActions extends CmsActions {

    public static final String showBlog="showBlog";
    public static final String openCreateEntry="openCreateEntry";
    public static final String openEditEntry="openEditEntry";
    public static final String saveEntry="saveEntry";
    public static final String deleteEntry="deleteEntry";

    public static final String KEY = "blog";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new BlogActions());
    }

    private BlogActions(){
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case showBlog: {
                return showBlog(request, response);
            }
            case openCreateEntry: {
                BlogEntryData data = new BlogEntryData();
                data.setPartId(RequestReader.getInt(request, "partId"));
                data.setId(BlogBean.getInstance().getNextId());
                data.setNew(true);
                SessionWriter.setSessionObject(request, "entry", data);
                return showCreateEntry(request, response);
            }
            case openEditEntry: {
                int id = RequestReader.getInt(request,"entryId");
                BlogEntryData data = BlogBean.getInstance().getBlogEntryData(id);
                SessionWriter.setSessionObject(request, "entry", data);
                return showEditEntry(request, response);
            }
            case saveEntry: {
                BlogEntryData data = (BlogEntryData) SessionReader.getSessionObject(request, "entry");
                if (data == null || data.getId() != RequestReader.getInt(request,"entryId"))
                    return false;
                if (!data.readRequestData(request)) {
                    return data.isNew() ? showCreateEntry(request, response) : showEditEntry(request, response);
                }
                data.prepareSave();
                BlogBean.getInstance().saveEntryData(data);
                return showBlog(request, response);
            }
            case deleteEntry: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                BlogBean.getInstance().deleteEntry(RequestReader.getInt(request,"entryId"));
                return showBlog(request, response);
            }
        }
        return false;
    }

    protected boolean showBlog(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/blog/blog.jsp");
    }

    protected boolean showCreateEntry(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/blog/createBlogEntry.jsp");
    }

    protected boolean showEditEntry(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/blog/editBlogEntry.jsp");
    }
}