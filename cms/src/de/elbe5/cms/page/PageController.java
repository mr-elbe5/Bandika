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
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageController extends Controller {

    public static final String KEY = "page";

    private static PageController instance=new PageController();

    public static PageController getInstance() {
        return instance;
    }

    @Override
    public String getKey(){
        return KEY;
    }

    public IActionResult toggleEditMode(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        rdata.setEditMode(!rdata.isEditMode());
        int pageId = rdata.getId();
        if (pageId == 0)
            pageId = PageCache.getInstance().getHomePageId(rdata.getSessionLocale());
        return new ForwardActionResult("/page/show/" + pageId);
    }

    public IActionResult show(RequestData rdata) {
        PageData data = getPageData(rdata);
        if (data == null)
            return noData(rdata);
        if (!data.isAnonymous() && !rdata.hasContentRight(data.getId(), Right.READ)) {
            return forbidden(rdata);
        }
        return new PageActionResult(data);
    }

    public IActionResult openCreatePage(RequestData rdata) {
        int parentId = rdata.getInt("parentId");
        if (!rdata.hasContentRight(parentId, Right.EDIT))
            return forbidden(rdata);
        PageData parentData = PageCache.getInstance().getPage(parentId);
        if (parentData == null)
            return noData(rdata);
        PageData data = new PageData();
        data.setCreateValues(parentData);
        data.setRanking(parentData.getSubPages().size());
        data.setAuthorName(rdata.getLoginName());
        rdata.setSessionObject(Statics.KEY_PAGE, data);
        return showEditPage();
    }

    public IActionResult openEditPage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = PageBean.getInstance().getPage(pageId);
        data.setEditValues(PageCache.getInstance().getPage(data.getId()));
        data.setViewMode(ViewMode.EDIT);
        rdata.setSessionObject(Statics.KEY_PAGE, data);
        return showEditPage();
    }

    public IActionResult savePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditPage();
        }
        data.setAuthorName(rdata.getLoginName());
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditPage();
        }
        rdata.removeSessionObject(Statics.KEY_PAGE);
        data.setViewMode(ViewMode.VIEW);
        data.setEditPagePart(null);
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageSaved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openPageStructure/" + data.getId());
    }

    public IActionResult clonePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData srcData = PageBean.getInstance().getPage(pageId);
        if (srcData == null)
            return noData(rdata);
        PageData data = new PageData();
        data.cloneData(srcData);
        data.setAuthorName(rdata.getLoginName());
        data.setViewMode(ViewMode.EDIT);
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditPage();
        }
        data.setViewMode(ViewMode.VIEW);
        data.setEditPagePart(null);
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageCloned.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/admin/openPageStructure?pageId=" + data.getId());
    }

    public IActionResult movePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        int parentId = rdata.getInt("parentId");
        PageCache cache = PageCache.getInstance();
        PageData parent = cache.getPage(parentId);
        if (parent == null)
            return badData(rdata);
        PageBean.getInstance().movePage(pageId, parentId);
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageMoved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/admin/openPageStructure?pageId=" + pageId);
    }

    public IActionResult deletePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        if (pageId < BaseIdData.ID_MIN) {
            rdata.setMessage(Strings._notDeletable.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("admin/openPageStructure");
        }
        PageCache cache = PageCache.getInstance();
        int parentId = cache.getParentPageId(pageId);
        PageBean.getInstance().deletePage(pageId);
        PageCache.getInstance().setDirty();
        rdata.put("pageId", Integer.toString(parentId));
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageDeleted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/admin/openPageStructure?pageId=" + parentId);
    }

    public IActionResult inheritAll(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = PageCache.getInstance().getPage(pageId);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        boolean anonymous = data.isAnonymous();
        List<PageData> pages = new ArrayList<>();
        data.getAllPages(pages);
        for (PageData page : pages) {
            page.setAuthorName(rdata.getLoginName());
            page.setAnonymous(anonymous);
            page.setInheritsRights(true);
            if (!PageBean.getInstance().savePage(page)) {
                Log.warn("could not inherit to page");
            }
        }
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._allInherited.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/admin/openPageStructure?pageId=" + pageId);
    }

    public IActionResult openEditPageContent(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = PageBean.getInstance().getPage(pageId);
        if (data == null)
            return noData(rdata);
        data.setEditValues(PageCache.getInstance().getPage(data.getId()));
        data.setViewMode(ViewMode.EDIT);
        rdata.setSessionObject(Statics.KEY_PAGE, data);
        data.setViewMode(ViewMode.EDIT);
        return new PageActionResult(data);
    }

    public IActionResult showEditPageContent(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null)
            return noData(rdata);
        data.setViewMode(ViewMode.EDIT);
        return setEditPageContentAjaxResponse(rdata, data);
    }

    public IActionResult savePageContent(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        data.setAuthorName(rdata.getLoginName());
        data.setDynamic();
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new PageActionResult(data);
        }
        rdata.removeSessionObject(Statics.KEY_PAGE);
        data.setViewMode(ViewMode.VIEW);
        data.setEditPagePart(null);
        PageCache.getInstance().setDirty();
        return show(rdata);
    }

    public IActionResult stopEditing(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        rdata.removeSessionObject(Statics.KEY_PAGE);
        return show(rdata);
    }

    public IActionResult publishPage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.APPROVE))
            return forbidden(rdata);
        if (!publish(pageId, rdata))
            return forbidden(rdata);
        return show(rdata);
    }

    public IActionResult getPageContent(RequestData rdata) {
        //only token secured calls
        int pageId = rdata.getId();
        String remoteHost = rdata.getRequest().getRemoteHost();
        Log.info("call from: " + remoteHost);
        String token = rdata.getString("token");
        if (!Token.matchToken(pageId, token)) {
            Log.warn("token does not match for id: " + pageId);
            return forbidden(rdata);
        }
        PageData data = PageBean.getInstance().getPage(pageId);
        data.setViewMode(ViewMode.PUBLISH);
        data.setPublishDate(PageBean.getInstance().getServerTime());
        data.setAuthorName(rdata.getLoginName());
        return setEditPageContentAjaxResponse(rdata, data);
    }

    private PageData getPageData(RequestData rdata) {
        PageData data;
        int pageId = rdata.getId();
        data = PageBean.getInstance().getPage(pageId);
        return data;
    }

    protected boolean publish(int pageId, RequestData rdata) {
        String token = Token.createToken(pageId);
        String uri = rdata.getSessionHost() +
                "/page/getPageContent/" +
                pageId +
                "?token=" +
                token;
        String result = callPageContent(uri);
        return result != null;
    }

    protected String callPageContent(String path) {
        try {
            Log.info("calling: " + path);
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
        } catch (Exception e) {
            Log.error("error while calling page " + path, e);
        }
        return null;
    }

    public IActionResult executePagePartMethod(RequestData rdata) {
        int pageId = rdata.getId();
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        String partMethod = rdata.getString("partMethod");
        PageData data = PageCache.getInstance().getPage(pageId);
        if (!data.isAnonymous() && !rdata.hasContentRight(pageId, Right.READ)) {
            return forbidden(rdata);
        }
        PagePartData pdata = data.getPagePart(sectionName, partId);
        if (pdata==null)
            return noData(rdata);
        return pdata.executePagePartMethod(partMethod, rdata);
    }

    public IActionResult openAddPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        return showAddPagePart();
    }

    public IActionResult addPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        String templateName = rdata.getString("template");
        int fromPartId = rdata.getInt("partId", -1);
        String mainClass = rdata.getString("flexClass");
        boolean below = rdata.getBoolean("below");
        String sectionName = rdata.getString("sectionName");
        PagePartData pdata = new PagePartData();
        pdata.setSectionName(sectionName);
        pdata.setTemplateName(templateName);
        pdata.setFlexClass(mainClass);
        pdata.setId(PageBean.getInstance().getNextId());
        pdata.setNew(true);
        data.addPagePart(pdata, fromPartId, below, true);
        data.setEditPagePart(pdata);
        rdata.setMessage(Strings._pagePartAdded.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/page/showEditPageContent/"+pageId, Statics.PAGE_CONTAINER_JQID);
    }

    public IActionResult openAddSharedPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        return showAddSharedPagePart();
    }

    public IActionResult addSharedPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("sharedPartId");
        int fromPartId = rdata.getInt("partId", -1);
        boolean below = rdata.getBoolean("below");
        String sectionName = rdata.getString("sectionName");
        PagePartData pdata = PagePartBean.getInstance().getPagePart(partId);
        if (pdata == null)
            return noData(rdata);
        pdata.setSectionName(sectionName);
        data.addSharedPagePart(pdata, fromPartId, below, true);
        rdata.setMessage(Strings._pagePartAdded.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/page/showEditPageContent/"+pageId, Statics.PAGE_CONTAINER_JQID);
    }

    public IActionResult editPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.setEditPagePart(sectionName, partId);
        return setPageContentResponse(rdata, data);
    }

    public IActionResult cancelEditPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        PagePartData pdata = data.getEditPagePart();
        if (pdata != null && pdata.getTemplateName().isEmpty()) {
            data.removePagePart(pdata.getSectionName(), pdata.getId());
        }
        data.setEditPagePart(null);
        return setPageContentResponse(rdata, data);
    }

    public IActionResult savePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        PagePartData pdata = data.getEditPagePart();
        if (pdata == null || data.getPagePart(sectionName, partId) != pdata) {
            return new PageActionResult(data);
        }
        pdata.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return new PageActionResult(data);
        }
        data.setEditPagePart(null);
        return setPageContentResponse(rdata, data);
    }

    public IActionResult openEditPagePartSettings(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.setEditPagePart(sectionName, partId);
        return showEditPagePartSettings();
    }

    public IActionResult savePagePartSettings(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        PagePartData part = data.getEditPagePart();
        if (part == null || part.getId() != partId)
            return badData(rdata);
        part.readPagePartSettingsData(rdata);
        data.setEditPagePart(null);
        rdata.setMessage(Strings._pagePartSettingsSaved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/page/showEditPageContent/"+pageId, Statics.PAGE_CONTAINER_JQID);
    }

    public IActionResult openSharePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.setEditPagePart(sectionName, partId);
        return showSharePagePart();
    }

    public IActionResult sharePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        PagePartData part = data.getEditPagePart();
        if (part == null || part.getId() != partId)
            return badData(rdata);
        part.setName(rdata.getString("name"));
        data.setEditPagePart(null);
        rdata.setMessage(Strings._pagePartShared.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/page/showEditPageContent/"+pageId, Statics.PAGE_CONTAINER_JQID);
    }

    public IActionResult movePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        int dir = rdata.getInt("dir");
        data.movePagePart(sectionName, partId, dir);
        return setPageContentResponse(rdata, data);
    }

    public IActionResult removePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = (PageData) rdata.getSessionObject(Statics.KEY_PAGE);
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.removePagePart(sectionName, partId);
        return setPageContentResponse(rdata, data);
    }

    public IActionResult deletePagePart(RequestData rdata) {
        int partId = rdata.getInt("partId");
        if (!PagePartBean.getInstance().deletePagePart(partId)) {
            //todo
            return forbidden(rdata);
        }
        rdata.setMessage(Strings._pagePartDeleted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openContentAdministration");
    }

    public IActionResult deleteAllOrphanedPageParts(RequestData rdata) {
        if (!PagePartBean.getInstance().deleteAllOrphanedPageParts()) {
            //todo
            return forbidden(rdata);
        }
        rdata.setMessage(Strings._pagePartsDeleted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openContentAdministration");
    }

    protected IActionResult setPageContentResponse(RequestData rdata, PageData data) {
        rdata.put(Statics.KEY_PAGE, data);
        return new ForwardActionResult("/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected IActionResult showAddPagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/addPagePart.ajax.jsp");
    }

    protected IActionResult showAddSharedPagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/addSharedPagePart.ajax.jsp");
    }

    protected IActionResult showEditPagePartSettings() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/editPagePartSettings.ajax.jsp");
    }

    protected IActionResult showSharePagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/sharePagePart.ajax.jsp");
    }

    protected IActionResult showDeletePagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/deletePagePart.ajax.jsp");
    }

    protected IActionResult setEditPageContentAjaxResponse(RequestData rdata, PageData data) {
        rdata.put(Statics.KEY_PAGE, data);
        return new ForwardActionResult("/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected IActionResult showEditPage() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/editPage.ajax.jsp");
    }

}
