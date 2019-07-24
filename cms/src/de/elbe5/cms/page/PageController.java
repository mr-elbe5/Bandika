/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.request.*;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.Controller;

import java.util.ArrayList;
import java.util.List;

public class PageController extends Controller {

    public static final String KEY = "page";

    private static PageController instance = new PageController();

    public static PageController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openPageAdministration(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/page/pageAdministration.jsp", Strings._pageAdministration.string(rdata.getSessionLocale()));
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
        String pageType = rdata.getString("pageType");
        PageData data = PageFactory.getPageData(pageType);
        if (data == null)
            return badData(rdata);
        data.setCreateValues(parentData);
        data.setRanking(parentData.getSubPages().size());
        data.setAuthorName(rdata.getUserName());
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
        rdata.setCurrentPage(data);
        return showEditPage();
    }

    public IActionResult savePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditPage();
        }
        data.setAuthorName(rdata.getUserName());
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditPage();
        }
        data.setNew(false);
        data.unsetDetailEditMode();
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageSaved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/page/openPageAdministration/" + data.getId());
    }

    public IActionResult clonePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData srcData = PageBean.getInstance().getPage(pageId);
        if (srcData == null)
            return noData(rdata);
        PageData data = PageFactory.getPageData("PageData");
        if (data == null)
            return badData(rdata);
        data.cloneData(srcData);
        data.setAuthorName(rdata.getUserName());
        data.setViewMode(ViewMode.EDIT);
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditPage();
        }
        data.setViewMode(ViewMode.VIEW);
        data.unsetDetailEditMode();
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageCloned.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/page/openPageAdministration?pageId=" + data.getId());
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
        return new ForwardActionResult("/page/openPageAdministration?pageId=" + pageId);
    }

    public IActionResult deletePage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        if (pageId < BaseIdData.ID_MIN) {
            rdata.setMessage(Strings._notDeletable.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("/page/openPageAdministration");
        }
        PageCache cache = PageCache.getInstance();
        int parentId = cache.getParentPageId(pageId);
        PageBean.getInstance().deletePage(pageId);
        PageCache.getInstance().setDirty();
        rdata.put("pageId", Integer.toString(parentId));
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._pageDeleted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/page/openPageAdministration?pageId=" + parentId);
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
            page.setAuthorName(rdata.getUserName());
            page.setAnonymous(anonymous);
            page.setInheritsRights(true);
            if (!PageBean.getInstance().savePage(page)) {
                Log.warn("could not inherit to page");
            }
        }
        PageCache.getInstance().setDirty();
        rdata.setMessage(Strings._allInherited.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/page/openPageAdministration?pageId=" + pageId);
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
        rdata.setCurrentPage(data);
        data.setViewMode(ViewMode.EDIT);
        return new PageActionResult(data);
    }

    public IActionResult showEditPageContent(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = rdata.getCurrentPage();
        if (data == null)
            return noData(rdata);
        data.setViewMode(ViewMode.EDIT);
        return setEditPageContentAjaxResponse(rdata, data);
    }

    public IActionResult savePageContent(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        PageData data = rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        data.setAuthorName(rdata.getUserName());
        if (!PageBean.getInstance().savePage(data)) {
            rdata.setMessage(Strings._saveError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new PageActionResult(data);
        }
        data.setViewMode(ViewMode.VIEW);
        data.unsetDetailEditMode();
        PageCache.getInstance().setDirty();
        return show(rdata);
    }

    public IActionResult stopEditing(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        return show(rdata);
    }

    public IActionResult publishPage(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.APPROVE))
            return forbidden(rdata);
        PageData data = PageBean.getInstance().getPage(pageId);
        if (data == null)
            return noData(rdata);
        data.setViewMode(ViewMode.PUBLISH);
        data.setPublishDate(PageBean.getInstance().getServerTime());
        data.setAuthorName(rdata.getUserName());
        return new PageActionResult(data);
    }

    private PageData getPageData(RequestData rdata) {
        PageData data;
        int pageId = rdata.getId();
        data = PageBean.getInstance().getPage(pageId);
        return data;
    }

    protected IActionResult setPageContentResponse(RequestData rdata, PageData data) {
        rdata.setCurrentPage(data);
        return new ForwardActionResult("/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected IActionResult setEditPageContentAjaxResponse(RequestData rdata, PageData data) {
        rdata.setCurrentPage(data);
        return new ForwardActionResult("/WEB-INF/_jsp/page/pageContent.inc.jsp");
    }

    protected IActionResult showEditPage() {
        return new ForwardActionResult("/WEB-INF/_jsp/page/editPage.ajax.jsp");
    }

}
