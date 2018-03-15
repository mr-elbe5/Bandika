/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.CkCallbackData;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.tree.BaseTreeActions;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FieldActions extends BaseTreeActions {

    public static final String openLinkBrowser="openLinkBrowser";
    public static final String showSelectableBrowserLinks="showSelectableBrowserLinks";
    public static final String openImageBrowser="openImageBrowser";
    public static final String reopenImageBrowser="reopenImageBrowser";
    public static final String showSelectableBrowserImages="showSelectableBrowserImages";
    public static final String openCreateImageInBrowser="openCreateImageInBrowser";
    public static final String saveImageInBrowser="saveImageInBrowser";


    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openLinkBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int siteId = RequestReader.getInt(request, "siteId");
                CkCallbackData browseData = new CkCallbackData();
                browseData.setPageId(pageId);
                browseData.setCkCallbackNum(RequestReader.getInt(request, "CKEditorFuncNum", -1));
                browseData.setSiteId(siteId);
                SessionWriter.setSessionObject(request, "browseData", browseData);
                return showLinkBrowserJsp(request, response);
            }
            case showSelectableBrowserLinks: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
                assert browseData != null;
                browseData.setSiteId(RequestReader.getInt(request, "siteId"));
                return showSelectableBrowserLinksJsp(request, response);
            }
            case openImageBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int siteId = RequestReader.getInt(request, "siteId");
                CkCallbackData browseData = new CkCallbackData();
                browseData.setPageId(pageId);
                browseData.setCkCallbackNum(RequestReader.getInt(request, "CKEditorFuncNum", -1));
                browseData.setSiteId(siteId);
                SessionWriter.setSessionObject(request, "browseData", browseData);
                return showImageBrowserJsp(request, response);
            }
            case reopenImageBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showImageBrowserJsp(request, response);
            }
            case showSelectableBrowserImages: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
                assert browseData != null;
                browseData.setSiteId(RequestReader.getInt(request, "siteId"));
                return showSelectableBrowserImagesJsp(request, response);
            }
            case openCreateImageInBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showCreateImageInBrowserJsp(request, response);
            }
            case saveImageInBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "siteId");
                FileBean ts = FileBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                FileData data = new FileData();
                data.readFileCreateRequestData(request);
                if (!data.isComplete()) {
                    return showCreateImageInBrowserJsp(request, response);
                }
                data.setNew(true);
                data.setId(FileBean.getInstance().getNextId());
                data.setParentId(parentNode.getId());
                data.setParent(parentNode);
                data.setAnonymous(parentNode.isAnonymous());
                data.setInheritsRights(true);
                data.inheritPathFromParent();
                data.inheritRightsFromParent();
                data.inheritParentIdsFromParent();
                data.setInNavigation(parentNode.isInNavigation());
                data.setRanking(parentNode.getSites().size());
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                ts.saveFile(data);
                TreeCache.getInstance().setDirty();
                return closeLayer(request, response, "closeLayerToBrowserLayer('/field.srv?act="+reopenImageBrowser+"&siteId=" + parentId + "&" + RequestStatics.KEY_MESSAGEKEY + "=_fileCreated');");
            }
            default: {
                return forbidden();
            }
        }
    }

    public static final String KEY = "field";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new FieldActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showImageBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browseImages.jsp");
    }

    public boolean showLinkBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browseLinks.jsp");
    }

    public boolean showSelectableBrowserLinksJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectableBrowserLinks.inc.jsp");
    }

    public boolean showSelectableBrowserImagesJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectableBrowserImages.inc.jsp");
    }

    public boolean showCreateImageInBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/createImageInBrowser.ajax.jsp");
    }

}
