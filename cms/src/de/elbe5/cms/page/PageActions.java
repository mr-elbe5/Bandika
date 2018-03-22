/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.cms.tree.BaseTreeActions;
import de.elbe5.cms.tree.TreeActions;
import de.elbe5.cms.tree.TreeBean;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.RightsCache;
import de.elbe5.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.Locale;

public class PageActions extends BaseTreeActions {

    public static final String show="show";
    public static final String showPageDetails="showPageDetails";
    public static final String openCreatePage="openCreatePage";
    public static final String createPage="createPage";
    public static final String openEditPageSettings="openEditPageSettings";
    public static final String savePageSettings="savePageSettings";
    public static final String openEditPageRights="openEditPageRights";
    public static final String savePageRights="savePageRights";
    public static final String clonePage="clonePage";
    public static final String cutPage="cutPage";
    public static final String movePage="movePage";
    public static final String deletePage="deletePage";
    public static final String openEditPageContent="openEditPageContent";
    public static final String reopenEditPageContent="reopenEditPageContent";
    public static final String savePageContent="savePageContent";
    public static final String publishPage="publishPage";
    public static final String stopEditing="stopEditing";
    public static final String showPageContent="showPageContent";

    public static final String KEY = "page";

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
            case showPageDetails: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showPageDetails(request, response);
            }
            case openCreatePage: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                return showCreatePage(request, response);
            }
            case createPage: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                PageData data = new PageData();
                data.setNew(true);
                int parentId = RequestReader.getInt(request, "siteId");
                String templateName = RequestReader.getString(request, "templateName");
                PageBean ts = PageBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                data.readPageCreateRequestData(request);
                data.setCreateValues(parentNode);
                data.setRanking(parentNode.getPages().size());
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setTemplateName(templateName);
                if (!isDataComplete(data, request)) {
                    request.setAttribute("siteData", parentNode);
                    return showCreatePage(request, response);
                }
                data.prepareSave();
                ts.savePage(data);
                data.stopEditing();
                data.prepareEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&siteId=" + data.getParentId() + "&pageId=" + data.getId());
            }
            case openEditPageSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData treeData=TreeCache.getInstance().getPage(pageId);
                checkObject(treeData);
                PageData data = PageBean.getInstance().getPage(pageId);
                data.setDefaultPage(treeData.isDefaultPage());
                data.setPath(treeData.getPath());
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                return showEditPageSettings(request, response);
            }
            case savePageSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.readPageSettingsRequestData(request);
                if (!data.isComplete()) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return showEditPageSettings(request, response);
                }
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                PageBean.getInstance().saveNodeSettings(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&pageId=" + data.getId(), "_pageSettingsChanged");
            }
            case openEditPageRights: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                PageData data = tc.getPage(pageId);
                checkObject(data);
                data = PageBean.getInstance().getPage(pageId);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                return showEditPageRights(request, response);
            }
            case savePageRights: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.readTreeNodeRightsData(request);
                PageBean.getInstance().saveRights(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&pageId=" + data.getId(), "_pageRightsChanged");
            }
            case clonePage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageBean ts = PageBean.getInstance();
                PageData treeData = TreeCache.getInstance().getPage(pageId);
                PageData srcData = PageBean.getInstance().getPage(pageId);
                checkObject(srcData);
                PageData data = new PageData();
                data.cloneData(srcData);
                checkObject(data);
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setDefaultPage(false);
                data.prepareEditing();
                ts.savePage(data);
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return showTree(request, response);
            }
            case cutPage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                SessionWriter.setSessionObject(request, "cutPageId", pageId);
                RequestWriter.setMessageKey(request, "_pageCut");
                return showTree(request, response);
            }
            case movePage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "parentId");
                TreeCache tc = TreeCache.getInstance();
                SiteData parent = tc.getSite(parentId);
                if (parent != null) {
                    TreeBean.getInstance().moveTreeNode(pageId, parentId);
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_pageMoved");
                } else {
                    return false;
                }
                return true;
            }
            case deletePage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                if (pageId < BaseIdData.ID_MIN) {
                    addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                    return showTree(request, response);
                }
                TreeCache tc = TreeCache.getInstance();
                int parentId = tc.getParentNodeId(pageId);
                PageBean.getInstance().deleteTreeNode(pageId);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                request.setAttribute("pageId", Integer.toString(parentId));
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                RequestWriter.setMessageKey(request, "_pageDeleted");
                request.setAttribute("siteId", Integer.toString(parentId));
                return showTree(request, response);
            }
            case openEditPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData treeData=TreeCache.getInstance().getPage(pageId);
                PageData data = PageBean.getInstance().getPage(pageId);
                if (treeData!=null){
                    data.setDefaultPage(treeData.isDefaultPage());
                    data.setPath(treeData.getPath());
                }
                checkObject(data);
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                data.setPageEditMode(true);
                return setPageResponse(request, response, data);
            }
            case reopenEditPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                data.setPageEditMode(true);
                return setPageResponse(request, response, data);
            }
            case savePageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                PageBean.getInstance().savePage(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return new PageActions().show(request, response);
            }
            case publishPage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.APPROVE))
                    return false;
                boolean fromAdmin = RequestReader.getBoolean(request, "fromAdmin");
                PageData data = PageBean.getInstance().getPage(pageId);
                data.setAuthorName(SessionReader.getLoginName(request));
                publish(data, SessionReader.getSessionLocale(request));
                RequestWriter.setMessageKey(request, "_pagePublished");
                request.setAttribute("siteId", Integer.toString(data.getParentId()));
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                if (fromAdmin) {
                    return showTree(request, response);
                }
                return new PageActions().show(request, response);
            }
            case stopEditing: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                SessionWriter.removeSessionObject(request, "pageData");
                return new PageActions().show(request, response);
            }
            case showPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
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
        int pageId = RequestReader.getInt(request, "pageId");
        TreeCache tc = TreeCache.getInstance();
        if (pageId == 0) {
            String url = request.getRequestURI();
            treeData = tc.getPage(url);
        } else {
            treeData = tc.getPage(pageId);
        }
        checkObject(treeData);
        if (pageId==0){
            pageId=treeData.getId();
            request.setAttribute("pageId", Integer.toString(pageId));
        }
        data = PageBean.getInstance().getPage(pageId);
        data.setPath(treeData.getPath());
        data.setDefaultPage(treeData.isDefaultPage());
        data.setParentIds(treeData.getParentIds());
        return data;
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) {
        PageData data = getPageData(request);
        if (!data.isAnonymous() && !SessionReader.hasContentRight(request, data.getId(), Right.READ)) {
            return forbidden();
        }
        return setPageResponse(request, response, data);
    }

    public static boolean publish(PageData data, Locale locale) {
        TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PAGE, data.getTemplateName());
        StringWriter writer=new StringWriter();
        PageOutputContext outputContext=new PageOutputContext(writer);
        PageOutputData outputData=new PageOutputData(data, locale);
        try {
            pageTemplate.writeTemplate(outputContext, outputData);
        } catch (Exception e) {
            Log.error("could not write page html", e);
        }
        data.setPublishedContent(writer.getBuffer().toString());
        PageBean.getInstance().publishPage(data);
        return true;
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

    protected boolean setEditPageContentAjaxResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        data.setPageEditMode(true);
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/content.ajax.jsp");
    }

    protected boolean showCreatePage(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/createPage.ajax.jsp");
    }

    protected boolean showEditPageSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageSettings.ajax.jsp");
    }

    protected boolean showEditPageRights(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageRights.ajax.jsp");
    }

    protected boolean showPageDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageDetails.ajax.jsp");
    }


}
