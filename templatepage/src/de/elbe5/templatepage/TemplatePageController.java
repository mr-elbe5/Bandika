/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.page.PageController;
import de.elbe5.request.*;
import de.elbe5.rights.Right;

public class TemplatePageController extends PageController {

    public static final String KEY = "templatepage";

    private static TemplatePageController instance = new TemplatePageController();

    public static TemplatePageController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openPartAdministration(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/templatepage/partAdministration.jsp", Strings.string("_partAdministration",rdata.getSessionLocale()));
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
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        String partType = rdata.getString("partType");
        int fromPartId = rdata.getInt("partId", -1);
        String mainClass = rdata.getString("flexClass");
        boolean below = rdata.getBoolean("below");
        String sectionName = rdata.getString("sectionName");
        PagePartData pdata = PagePartFactory.getPagePartData(partType);
        if (pdata == null)
            return badData(rdata);
        pdata.setSectionName(sectionName);
        pdata.setCreateValues(rdata);
        pdata.setFlexClass(mainClass);
        pdata.setId(TemplatePageBean.getInstance().getNextId());
        pdata.setNew(true);
        data.addPagePart(pdata, fromPartId, below, true);
        data.setEditPagePart(pdata);
        rdata.setMessage(Strings.string("_pagePartAdded",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/page/showEditPageContent/" + pageId + "#current");
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
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("sharedPartId");
        int fromPartId = rdata.getInt("partId", -1);
        boolean below = rdata.getBoolean("below");
        String sectionName = rdata.getString("sectionName");
        PagePartData pdata = TemplatePageBean.getInstance().getPagePart(partId);
        if (pdata == null)
            return noData(rdata);
        pdata.setSectionName(sectionName);
        data.addSharedPagePart(pdata, fromPartId, below, true);
        rdata.setMessage(Strings.string("_pagePartAdded",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/page/showEditPageContent/" + pageId + "#current");
    }

    public IActionResult editPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.setEditPagePart(sectionName, partId);
        rdata.setCurrentPage(data);
        return new PageActionResult(data);
    }

    public IActionResult cancelEditPagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        PagePartData pdata = data.getEditPagePart();
        if (pdata != null && pdata.isNew()) {
            data.removePagePart(pdata.getSectionName(), pdata.getId());
        }
        data.setEditPagePart(null);
        rdata.setCurrentPage(data);
        return new PageActionResult(data);
    }

    public IActionResult savePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
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
        rdata.setCurrentPage(data);
        return new PageActionResult(data);
    }

    public IActionResult openEditPagePartSettings(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.setEditPagePart(sectionName, partId);
        PagePartData pdata = data.getEditPagePart();
        return showEditPagePartSettings(pdata);
    }

    public IActionResult savePagePartSettings(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        PagePartData part = data.getEditPagePart();
        if (part == null || part.getId() != partId)
            return badData(rdata);
        part.readPagePartSettingsData(rdata);
        data.setEditPagePart(null);
        rdata.setMessage(Strings.string("_pagePartSettingsSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/page/showEditPageContent/" + pageId + "#current");
    }

    public IActionResult openSharePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
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
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null)
            return noData(rdata);
        int partId = rdata.getInt("partId");
        PagePartData part = data.getEditPagePart();
        if (part == null || part.getId() != partId)
            return badData(rdata);
        part.setName(rdata.getString("name"));
        data.setEditPagePart(null);
        rdata.setMessage(Strings.string("_pagePartShared",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/page/showEditPageContent/" + pageId);
    }

    public IActionResult movePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        int dir = rdata.getInt("dir");
        data.movePagePart(sectionName, partId, dir);
        rdata.setCurrentPage(data);
        return new PageActionResult(data);
    }

    public IActionResult removePagePart(RequestData rdata) {
        int pageId = rdata.getId();
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
        if (data == null || data.getId() != pageId)
            return badData(rdata);
        int partId = rdata.getInt("partId");
        String sectionName = rdata.getString("sectionName");
        data.removePagePart(sectionName, partId);
        rdata.setCurrentPage(data);
        return new PageActionResult(data);
    }

    public IActionResult deletePagePart(RequestData rdata) {
        int partId = rdata.getInt("partId");
        if (!TemplatePageBean.getInstance().deletePagePart(partId)) {
            //todo
            return forbidden(rdata);
        }
        rdata.setMessage(Strings.string("_pagePartDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/templatepage/openPartAdministration");
    }

    public IActionResult deleteAllOrphanedPageParts(RequestData rdata) {
        if (!TemplatePageBean.getInstance().deleteAllOrphanedPageParts()) {
            //todo
            return forbidden(rdata);
        }
        rdata.setMessage(Strings.string("_pagePartsDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/templatepage/openPartAdministration");
    }

    protected IActionResult showAddPagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/templatepage/addPagePart.ajax.jsp");
    }

    protected IActionResult showAddSharedPagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/templatepage/addSharedPagePart.ajax.jsp");
    }

    protected IActionResult showEditPagePartSettings(PagePartData part) {
        return new ForwardActionResult(part.getSettingsInclude());
    }

    protected IActionResult showSharePagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/templatepage/sharePagePart.ajax.jsp");
    }

    protected IActionResult showDeletePagePart() {
        return new ForwardActionResult("/WEB-INF/_jsp/templatepage/deletePagePart.ajax.jsp");
    }

}
