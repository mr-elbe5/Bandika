/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.application.AdminPage;
import de.elbe5.base.Log;
import de.elbe5.base.Strings;
import de.elbe5.base.BaseData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.ForwardResponse;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.response.IResponse;
import de.elbe5.servlet.ResponseException;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class ContentController extends Controller {

    public static final String KEY = "content";

    private static ContentController instance = null;

    public static void setInstance(ContentController instance) {
        ContentController.instance = instance;
    }

    public static ContentController getInstance() {
        return instance;
    }

    public static void register(ContentController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    //frontend
    public IResponse show(RequestData rdata) {
        //Log.log("show");
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserReadRight(rdata));
        ContentBean.getInstance().increaseViewCount(data.getId());
        return showContent(data);
    }

    //frontend
    public IResponse show(String url, RequestData rdata) {
        ContentData data;
        data = ContentCache.getContent(url);
        checkRights(data.hasUserReadRight(rdata));
        //Log.log("show: "+data.getClass().getSimpleName());
        ContentBean.getInstance().increaseViewCount(data.getId());
        return showContent(data);
    }

    //backend
    public IResponse openCreateContentData(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        checkRights(parentData.hasUserEditRight(rdata));
        String type = rdata.getAttributes().getString("type");
        ContentData data = ContentFactory.getNewData(type);
        data.setCreateValues(parentData, rdata);
        data.setRanking(parentData.getChildren().size());
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditContentData(rdata, data);
    }

    //backend
    public IResponse openEditContentData(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditContentData(rdata, data);
    }

    //backend
    public IResponse saveContentData(RequestData rdata) {
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        if (data.isNew())
            data.readCreateRequestData(rdata);
        else
            data.readUpdateRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditContentData(rdata, data);
        }
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveContent(data)) {
            setSaveError(rdata);
            return showEditContentData(rdata, data);
        }
        data.setNew(false);
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId(), Strings.getString("_contentSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    //backend
    public IResponse openEditRights(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditRights(rdata, data);
    }

    //backend
    public IResponse saveRights(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        data.readRightsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditRights(rdata, data);
        }
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveRights(data)) {
            setSaveError(rdata);
            return showEditRights(rdata, data);
        }
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId(), Strings.getString("_rightsSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    //backend
    public IResponse cutContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setClipboardData(ContentRequestKeys.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse copyContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData srcData = ContentBean.getInstance().getContent(contentId);
        checkRights(srcData.hasUserEditRight(rdata));
        ContentData data = ContentFactory.getNewData(srcData.getType());
        data.copyData(srcData, rdata);
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(ContentRequestKeys.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse pasteContent(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ContentData data=rdata.getClipboardData(ContentRequestKeys.KEY_CONTENT,ContentData.class);
        if (data==null){
            rdata.setMessage(Strings.getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        ContentData parent = ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        Set<Integer> parentIds=new HashSet<>();
        parent.collectParentIds(parentIds);
        if (parentIds.contains(data.getId())){
            rdata.setMessage(Strings.getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        data.setParentId(parentId);
        data.setParent(parent);
        data.generatePath();
        data.setChangerId(rdata.getUserId());
        ContentBean.getInstance().saveContent(data);
        rdata.clearClipboardData(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.getString("_contentPasted"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse clearClipboard(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.CONTENTEDIT));
        rdata.clearAllClipboardData();
        return showContentAdministration(rdata);
    }

    //backend
    public IResponse deleteContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data=ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata)) ;
        if (contentId < BaseData.ID_MIN) {
            rdata.setMessage(Strings.getString("_notDeletable"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        int parentId = ContentCache.getParentContentId(contentId);
        ContentBean.getInstance().deleteContent(contentId);
        ContentCache.setDirty();
        rdata.getAttributes().put("contentId", Integer.toString(parentId));
        ContentCache.setDirty();
        rdata.setMessage(Strings.getString("_contentDeleted"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,parentId);
    }

    //backend
    public IResponse openSortChildPages(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showSortChildContents(rdata);
    }

    //backend
    public IResponse saveChildPageRanking(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        for (ContentData child : data.getChildren()){
            int ranking=rdata.getAttributes().getInt("select"+child.getId(),-1);
            if (ranking!=-1){
                child.setRanking(ranking);

            }
        }
        Collections.sort(data.getChildren());
        ContentBean.getInstance().updateChildRankings(data);
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId, Strings.getString("_newRankingSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    public IResponse openCreateContentFrontend(RequestData rdata) {
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    //frontend
    public IResponse openEditContentFrontend(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId,ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentBean.getInstance().getContent(data.getId()), rdata);
        data.startEditing();
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return data.getResponse();
    }

    public IResponse showEditContentFrontend(RequestData rdata) {
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        return data.getResponse();
    }

    public IResponse saveContentFrontend(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        data.readFrontendRequestData(rdata);
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveContent(data)) {
            setSaveError(rdata);
            return data.getResponse();
        }
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        data.stopEditing();
        ContentCache.setDirty();
        return show(rdata);
    }

    public IResponse cancelEditContentFrontend(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        checkRights(data.hasUserEditRight(rdata));
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        data.stopEditing();
        return show(rdata);
    }

    public IResponse showDraft(RequestData rdata){
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserReadRight(rdata));
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        return data.getResponse();
    }

    public IResponse showPublished(RequestData rdata){
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserReadRight(rdata));
        data.setViewType(ContentData.VIEW_TYPE_SHOWPUBLISHED);
        return data.getResponse();
    }

    public IResponse publishContent(RequestData rdata){
        int contentId = rdata.getId();
        Log.log("Publishing content id " + contentId);
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserApproveRight(rdata));
        data.publish(rdata);
        return data.getResponse();
    }

    protected IResponse showContent(ContentData contentData) {
        return contentData.getResponse();
    }

    protected IResponse showEditContentData(RequestData rdata, ContentData contentData) {
        return contentData.getContentDataPage().createHtml(rdata);
    }

    protected IResponse showEditRights(RequestData rdata, ContentData contentData) {
        return new EditContentRightsPage().createHtml(rdata);
    }

    protected IResponse showSortChildContents(RequestData rdata) {
        return new SortChildContentPage().createHtml(rdata);
    }

    protected IResponse showContentAdministration(RequestData rdata) {
        return new AdminPage(new ContentAdminPage(), Strings.getString("_contentAdministration"));
    }

    protected IResponse showContentAdministration(RequestData rdata, int contentId) {
        rdata.getAttributes().put("contentId", contentId);
        return showContentAdministration(rdata);
    }

}
