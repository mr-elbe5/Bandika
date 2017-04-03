/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.field;

import de.bandika.file.FileBean;
import de.bandika.file.FileData;
import de.bandika.pagepart.CkCallbackData;
import de.bandika.rights.Right;
import de.bandika.servlet.*;
import de.bandika.site.SiteData;
import de.bandika.tree.ITreeAction;
import de.bandika.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum FieldAction implements ITreeAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    },
    // browser actions used for ckeditor and direct selection
    /**
     * opens tree browser in window for selecting a link
     */
    openLinkBrowser {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    }, /**
     * shows links of a certain site in the tree browser window
     */
    showSelectableBrowserLinks {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
                    assert browseData != null;
                    browseData.setSiteId(RequestReader.getInt(request, "siteId"));
                    return showSelectableBrowserLinksJsp(request, response);
                }
            }, /**
     * opens tree browser in window for selecting an image
     */
    openImageBrowser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            }, /**
     * refreshes the image tree browser window
     */
    reopenImageBrowser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    return showImageBrowserJsp(request, response);
                }
            }, /**
     * shows images of a certain site in the tree browser window
     */
    showSelectableBrowserImages {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
                    assert browseData != null;
                    browseData.setSiteId(RequestReader.getInt(request, "siteId"));
                    return showSelectableBrowserImagesJsp(request, response);
                }
            }, /**
     * opens dialog for creating a new image in the tree browser window
     */
    openCreateImageInBrowser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    return showCreateImageInBrowserJsp(request, response);
                }
            }, /**
     * saves a new image in the tree browser window
     */
    saveImageInBrowser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                    data.setPublished(true);
                    ts.createFile(data);
                    TreeCache.getInstance().setDirty();
                    return closeLayer(request, response, "closeLayerToBrowserLayer('/field.srv?act=reopenImageBrowser&siteId=" + parentId + "&" + RequestStatics.KEY_MESSAGEKEY + "=_fileCreated');");
                }
            };

    public static final String KEY = "field";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, FieldAction.class);
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
