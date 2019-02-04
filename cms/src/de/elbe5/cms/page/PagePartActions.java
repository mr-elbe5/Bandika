/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.JsonData;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PagePartActions extends ActionSet {

    public static final String executePagePartMethod="executePagePartMethod";
    public static final String openAddPagePart="openAddPagePart";
    public static final String addPagePart="addPagePart";
    public static final String openAddSharedPagePart="openAddSharedPagePart";
    public static final String addSharedPagePart="addSharedPagePart";
    public static final String editPagePart="editPagePart";
    public static final String cancelEditPagePart="cancelEditPagePart";
    public static final String savePagePart="savePagePart";
    public static final String openEditPagePartSettings="openEditPagePartSettings";
    public static final String savePagePartSettings="savePagePartSettings";
    public static final String openSharePagePart="openSharePagePart";
    public static final String sharePagePart="sharePagePart";
    public static final String movePagePart="movePagePart";
    public static final String removePagePart="removePagePart";
    public static final String showPagePartDetails="showPagePartDetails";
    public static final String deletePagePart="deletePagePart";
    public static final String deleteAllOrphanedPageParts="deleteAllOrphanedPageParts";

    public static final String KEY_PART = "partData";
    public static final String KEY_PART_ID = "partId";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case executePagePartMethod: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                String partMethod = RequestReader.getString(request, "partMethod");
                PageData data = PageCache.getInstance().getPage(pageId);
                if (!data.isAnonymous() && !SessionReader.hasContentRight(request, pageId, Right.READ)) {
                    return forbidden(request, response);
                }
                PagePartData pdata = data.getPagePart(sectionName, partId);
                return pdata != null && pdata.executePagePartMethod(partMethod, request, response);
            }
            case openAddPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showAddPagePart(request, response);
            }
            case addPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                String templateName=RequestReader.getString(request, "template");
                int fromPartId = RequestReader.getInt(request, KEY_PART_ID, -1);
                String mainClass= RequestReader.getString(request, "flexClass");
                boolean below = RequestReader.getBoolean(request, "below");
                String sectionName = RequestReader.getString(request, "sectionName");
                PagePartData pdata = new PagePartData();
                pdata.setSectionName(sectionName);
                pdata.setTemplateName(templateName);
                pdata.setFlexClass(mainClass);
                pdata.setId(PageBean.getInstance().getNextId());
                pdata.setNew(true);
                data.addPagePart(pdata, fromPartId, below, true);
                data.setEditPagePart(pdata);
                JsonData jdata=new JsonData();
                jdata.AddParam("act",PageActions.showEditPageContent);
                jdata.AddParam(Statics.KEY_MESSAGEKEY, Strings._pagePartAdded.toString());
                return closeDialogWithAjaxRedirect(request,response,getPostByAjaxCall("/page.srv", jdata, Statics.PAGE_CONTAINER_JQID));
            }
            case openAddSharedPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showAddSharedPagePart(request, response);
            }
            case addSharedPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                int partId=RequestReader.getInt(request, "sharedPartId");
                int fromPartId = RequestReader.getInt(request, KEY_PART_ID, -1);
                boolean below = RequestReader.getBoolean(request, "below");
                String sectionName = RequestReader.getString(request, "sectionName");
                PagePartData pdata = PagePartBean.getInstance().getPagePart(partId);
                if (pdata==null)
                    return noData(request,response);
                pdata.setSectionName(sectionName);
                data.addSharedPagePart(pdata, fromPartId, below, true);
                JsonData jdata=new JsonData();
                jdata.AddParam("act",PageActions.showEditPageContent);
                jdata.AddParam(Statics.KEY_MESSAGEKEY,Strings._pagePartAdded.toString());
                return closeDialogWithAjaxRedirect(request,response,getPostByAjaxCall("/page.srv", jdata, Statics.PAGE_CONTAINER_JQID));
            }
            case editPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return setPageContentResponse(request, response, data);
            }
            case cancelEditPagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                PagePartData pdata = data.getEditPagePart();
                if (pdata != null && pdata.getTemplateName().isEmpty()) {
                    data.removePagePart(pdata.getSectionName(), pdata.getId());
                }
                data.setEditPagePart(null);
                return setPageContentResponse(request, response, data);
            }
            case savePagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                PagePartData pdata = data.getEditPagePart();
                if (pdata == null || data.getPagePart(sectionName, partId) != pdata) {
                    return setPageResponse(request, response, data);
                }
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)){
                    return setPageResponse(request, response, data);
                }
                data.setEditPagePart(null);
                return setPageContentResponse(request, response, data);
            }
            case openEditPagePartSettings: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null)
                    return noData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return showEditPagePartSettings(request, response);
            }
            case savePagePartSettings: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null)
                    return noData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                PagePartData part = data.getEditPagePart();
                if (part==null || part.getId()!=partId)
                    return badData(request,response);
                part.readPagePartSettingsData(request);
                data.setEditPagePart(null);
                JsonData jdata=new JsonData();
                jdata.AddParam("act",PageActions.showEditPageContent);
                jdata.AddParam(Statics.KEY_MESSAGEKEY,Strings._pagePartSettingsSaved.toString());
                return closeDialogWithAjaxRedirect(request,response,getPostByAjaxCall("/page.srv", jdata, Statics.PAGE_CONTAINER_JQID));
            }
            case openSharePagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null)
                    return noData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return showSharePagePart(request, response);
            }
            case sharePagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null)
                    return noData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                PagePartData part = data.getEditPagePart();
                if (part==null || part.getId()!=partId)
                    return badData(request,response);
                part.setName(RequestReader.getString(request, "name"));
                data.setEditPagePart(null);
                JsonData jdata=new JsonData();
                jdata.AddParam("act",PageActions.showEditPageContent);
                jdata.AddParam(Statics.KEY_MESSAGEKEY,Strings._pagePartShared.toString());
                return closeDialogWithAjaxRedirect(request,response,getPostByAjaxCall("/page.srv", jdata, Statics.PAGE_CONTAINER_JQID));
            }
            case movePagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                int dir = RequestReader.getInt(request, "dir");
                data.movePagePart(sectionName, partId, dir);
                return setPageContentResponse(request, response, data);
            }
            case removePagePart: {
                int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, ActionSet.KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                String sectionName = RequestReader.getString(request, "sectionName");
                data.removePagePart(sectionName, partId);
                return setPageContentResponse(request, response, data);
            }
            case showPagePartDetails: {
                return showPagePartDetails(request, response);
            }
            case deletePagePart: {
                int partId = RequestReader.getInt(request, KEY_PART_ID);
                if (!PagePartBean.getInstance().deletePagePart(partId)){
                    //todo
                    return forbidden(request,response);
                }
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openContentAdministration,Strings._pagePartDeleted);
            }
            case deleteAllOrphanedPageParts: {
                if (!PagePartBean.getInstance().deleteAllOrphanedPageParts()){
                    //todo
                    return forbidden(request,response);
                }
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openContentAdministration,Strings._pagePartsDeleted);
            }
            default: {
                return forbidden(request, response);
            }
        }
    }

    public static final String KEY = "pagepart";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new PagePartActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean setPageContentResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute(ActionSet.KEY_PAGE, data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected boolean showAddPagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/addPagePart.ajax.jsp");
    }

    protected boolean showAddSharedPagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/addSharedPagePart.ajax.jsp");
    }

    protected boolean showEditPagePartSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPagePartSettings.ajax.jsp");
    }

    protected boolean showSharePagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharePagePart.ajax.jsp");
    }

    protected boolean showPagePartDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pagePartDetails.ajax.jsp");
    }

    protected boolean showDeletePagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deletePagePart.ajax.jsp");
    }

}
