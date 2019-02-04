/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.Token;
import de.elbe5.base.log.Log;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageActions extends ActionSet {

    public static final String show="show";
    public static final String openCreatePage="openCreatePage";
    public static final String openEditPage ="openEditPage";
    public static final String savePage ="savePage";
    public static final String clonePage="clonePage";
    public static final String movePage="movePage";
    public static final String deletePage="deletePage";
    public static final String openEditPageContent="openEditPageContent";
    public static final String showEditPageContent ="showEditPageContent";
    public static final String savePageContent="savePageContent";
    public static final String stopEditing="stopEditing";
    public static final String publishPage="publishPage";

    private static final String getPageContent ="getPageContent";
    
    public static final String inheritAll = "inheritsRights";

    public static final String KEY = "page";
    public static final String KEY_PAGE_ID = "pageId";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new PageActions());
    }

    private PageActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case show: {
                return show(request, response);
            }
            case openCreatePage: {
                int parentId = RequestReader.getInt(request, "parentId");
                if (!hasContentRight(request, parentId, Right.EDIT))
                    return forbidden(request,response);
                PageData parentData=PageCache.getInstance().getPage(parentId);
                if (parentData==null)
                    return noData(request,response);
                PageData data = new PageData();
                data.setCreateValues(parentData);
                data.setRanking(parentData.getSubPages().size());
                data.setAuthorName(SessionReader.getLoginName(request));
                SessionWriter.setSessionObject(request, KEY_PAGE, data);
                return showEditPage(request, response);
            }
            case openEditPage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = PageBean.getInstance().getPage(pageId);
                data.setEditValues(PageCache.getInstance().getPage(data.getId()));
                data.setViewMode(ViewMode.EDIT);
                SessionWriter.setSessionObject(request, KEY_PAGE, data);
                return showEditPage(request, response);
            }
            case savePage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)) {
                    return showEditPage(request, response);
                }
                data.setAuthorName(SessionReader.getLoginName(request));
                if (!PageBean.getInstance().savePage(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return showEditPage(request, response);
                }
                SessionWriter.removeSessionObject(request, KEY_PAGE);
                data.setViewMode(ViewMode.VIEW);
                data.setEditPagePart(null);
                PageCache.getInstance().setDirty();
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openPageStructure+"&pageId=" + data.getId(),Strings._pageSaved);
            }
            case clonePage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData srcData = PageBean.getInstance().getPage(pageId);
                if (srcData==null)
                    return noData(request,response);
                PageData data = new PageData();
                data.cloneData(srcData);
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setViewMode(ViewMode.EDIT);
                if (!PageBean.getInstance().savePage(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return showEditPage(request, response);
                }
                data.setViewMode(ViewMode.VIEW);
                data.setEditPagePart(null);
                PageCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request,Strings._pageCloned);
                return sendForwardResponse(request,response,"/admin.ajx?act="+ AdminActions.openPageStructure+"&pageId="+data.getId());
            }
            case movePage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                int parentId = RequestReader.getInt(request, "parentId");
                PageCache cache = PageCache.getInstance();
                PageData parent = cache.getPage(parentId);
                if (parent != null) {
                    PageBean.getInstance().movePage(pageId, parentId);
                    PageCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, Strings._pageMoved.toString());
                } else {
                    return badData(request,response);
                }
                return true;
            }
            case deletePage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                if (pageId < BaseIdData.ID_MIN) {
                    ErrorMessage.setMessageByKey(request, Strings._notDeletable);
                    return sendForwardResponse(request,response,"admin.srv?act="+AdminActions.openPageStructure);
                }
                PageCache cache = PageCache.getInstance();
                int parentId = cache.getParentPageId(pageId);
                PageBean.getInstance().deletePage(pageId);
                PageCache.getInstance().setDirty();
                request.setAttribute(KEY_PAGE_ID, Integer.toString(parentId));
                PageCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request,Strings._pageDeleted);
                return sendForwardResponse(request,response,"/admin.ajx?act="+ AdminActions.openPageStructure+"&pageId="+parentId);
            }
            case inheritAll: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = PageCache.getInstance().getPage(pageId);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                boolean anonymous = data.isAnonymous();
                List<PageData> pages = new ArrayList<>();
                data.getAllPages(pages);
                for (PageData page : pages) {
                    page.setAuthorName(SessionReader.getLoginName(request));
                    page.setAnonymous(anonymous);
                    page.setInheritsRights(true);
                    if (!PageBean.getInstance().savePage(page)){
                        Log.warn("could not inherit to page");
                    }
                }
                PageCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request,Strings._allInherited);
                return sendForwardResponse(request,response,"/admin.ajx?act="+ AdminActions.openPageStructure+"&pageId="+pageId);
            }
            case openEditPageContent: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = PageBean.getInstance().getPage(pageId);
                if (data==null)
                    return noData(request,response);
                data.setEditValues(PageCache.getInstance().getPage(data.getId()));
                data.setViewMode(ViewMode.EDIT);
                SessionWriter.setSessionObject(request, KEY_PAGE, data);
                data.setViewMode(ViewMode.EDIT);
                return setPageResponse(request, response, data);
            }
            case showEditPageContent: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, KEY_PAGE);
                if (data==null)
                    return noData(request,response);
                setSuccessMessageByKey(request);
                data.setViewMode(ViewMode.EDIT);
                return setEditPageContentAjaxResponse(request, response, data);
            }
            case savePageContent: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                PageData data = (PageData) RequestReader.getSessionObject(request, KEY_PAGE);
                if (data==null || data.getId()!=pageId)
                    return badData(request,response);
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setDynamic();
                if (!PageBean.getInstance().savePage(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return setPageResponse(request, response, data);
                }
                SessionWriter.removeSessionObject(request, KEY_PAGE);
                data.setViewMode(ViewMode.VIEW);
                data.setEditPagePart(null);
                PageCache.getInstance().setDirty();
                return show(request, response);
            }
            case stopEditing: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                SessionWriter.removeSessionObject(request, KEY_PAGE);
                return show(request, response);
            }
            case publishPage: {
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                if (!hasContentRight(request, pageId, Right.APPROVE))
                    return forbidden(request,response);
                if (!publish(pageId, request))
                    return forbidden(request,response);
                return show(request,response);
            }
            case getPageContent: {
                //only token secured calls
                int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
                String remoteHost=request.getRemoteHost();
                Log.info("call from: "+remoteHost);
                String token=RequestReader.getString(request,"token");
                if (!Token.matchToken(pageId,token)) {
                    Log.warn("token does not match for id: "+pageId);
                    return forbidden(request, response);
                }
                PageData data = PageBean.getInstance().getPage(pageId);
                data.setViewMode(ViewMode.PUBLISH);
                data.setPublishDate(PageBean.getInstance().getServerTime());
                data.setAuthorName(SessionReader.getLoginName(request));
                return setEditPageContentAjaxResponse(request, response, data);
            }
            default: {
                return show(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    private PageData getPageData(HttpServletRequest request) {
        PageData treeData, data;
        int pageId = RequestReader.getInt(request, KEY_PAGE_ID);
        PageCache tc = PageCache.getInstance();
        if (pageId == 0) {
            String url = request.getRequestURI();
            treeData = tc.getPage(url);
        } else {
            treeData = tc.getPage(pageId);
        }
        if (treeData==null)
            return null;
        if (pageId==0){
            pageId=treeData.getId();
            request.setAttribute(KEY_PAGE_ID, Integer.toString(pageId));
        }
        data = PageBean.getInstance().getPage(pageId);
        data.setPath(treeData.getPath());
        return data;
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) {
        PageData data = getPageData(request);
        if (data==null)
            return noData(request,response);
        if (!data.isAnonymous() && !SessionReader.hasContentRight(request, data.getId(), Right.READ)) {
            return forbidden(request, response);
        }
        return setPageResponse(request, response, data);
    }

    protected boolean publish(int pageId, HttpServletRequest request){
        String token= Token.createToken(pageId);
        String uri = RequestReader.getHostUrl(request) +
                "/page.ajx?act=getPageContent&pageId=" +
                pageId +
                "&token=" +
                token;
        String result=callPageContent(uri);
        return result!=null;
    }

    protected String callPageContent(String path){
        try {
            Log.info("calling: "+path);
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(20000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        }
        catch(Exception e){
            Log.error("error while calling page "+path,e);
        }
        return null;
    }

    protected boolean setEditPageContentAjaxResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute(KEY_PAGE, data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected boolean showEditPage(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPage.ajax.jsp");
    }

}
