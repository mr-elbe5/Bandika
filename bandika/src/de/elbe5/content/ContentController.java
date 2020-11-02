/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.data.Strings;
import de.elbe5.base.data.BaseData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.CmsAuthorizationException;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;

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
    public IResponse show(SessionRequestData rdata) {
        //Log.log("show");
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        ContentBean.getInstance().increaseViewCount(data.getId());
        return data.getDefaultView();
    }

    //frontend
    public IResponse show(String url, SessionRequestData rdata) {
        ContentData data;
        data = ContentCache.getContent(url);
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        //Log.log("show: "+data.getClass().getSimpleName());
        ContentBean.getInstance().increaseViewCount(data.getId());
        return data.getDefaultView();
    }

    //backend
    public IResponse openCreateContentData(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        assert(parentData!=null);
        checkRights(parentData.hasUserEditRight(rdata));
        String type = rdata.getString("type");
        ContentData data = ContentFactory.getNewData(type);
        assert(data!=null);
        data.setCreateValues(parentData, rdata);
        data.setRanking(parentData.getChildren().size());
        rdata.setSessionObject(RequestData.KEY_CONTENT, data);
        return showEditContentData(data);
    }

    //backend
    public IResponse openEditContentData(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setCurrentSessionContent(data);
        return showEditContentData(data);
    }

    //backend
    public IResponse saveContentData(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getCurrentSessionContent();
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        if (data.isNew())
            data.readCreateRequestData(rdata);
        else
            data.readUpdateRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditContentData(data);
        }
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveContent(data)) {
            setSaveError(rdata);
            return showEditContentData(data);
        }
        data.setNew(false);
        rdata.removeCurrentSessionContent();
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    //backend
    public IResponse openEditRights(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setCurrentSessionContent(data);
        return showEditRights(data);
    }

    //backend
    public IResponse saveRights(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getCurrentSessionContent();
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.readRightsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditRights(data);
        }
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveRights(data)) {
            setSaveError(rdata);
            return showEditRights(data);
        }
        rdata.removeCurrentSessionContent();
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_rightsSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    //backend
    public IResponse cutContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setClipboardData(RequestData.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse copyContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData srcData = ContentBean.getInstance().getContent(contentId);
        assert(srcData!=null);
        checkRights(srcData.hasUserEditRight(rdata));
        ContentData data = ContentFactory.getNewData(srcData.getType());
        assert(data!=null);
        data.copyData(srcData, rdata);
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(RequestData.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse pasteContent(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData data=rdata.getClipboardData(RequestData.KEY_CONTENT,ContentData.class);
        if (data==null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        ContentData parent = ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        Set<Integer> parentIds=new HashSet<>();
        parent.collectParentIds(parentIds);
        if (parentIds.contains(data.getId())){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        data.setParentId(parentId);
        data.setParent(parent);
        data.generatePath();
        data.setChangerId(rdata.getUserId());
        ContentBean.getInstance().saveContent(data);
        rdata.clearClipboardData(RequestData.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentPasted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse deleteContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data=ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata)) ;
        if (contentId < BaseData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        int parentId = ContentCache.getParentContentId(contentId);
        ContentBean.getInstance().deleteContent(contentId);
        ContentCache.setDirty();
        rdata.put("contentId", Integer.toString(parentId));
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,parentId);
    }

    //backend
    public IResponse openSortChildPages(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setCurrentSessionContent(data);
        return showSortChildContents();
    }

    //backend
    public IResponse saveChildPageRanking(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getCurrentSessionContent();
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        for (ContentData child : data.getChildren()){
            int ranking=rdata.getInt("select"+child.getId(),-1);
            if (ranking!=-1){
                child.setRanking(ranking);

            }
        }
        Collections.sort(data.getChildren());
        ContentBean.getInstance().updateChildRankings(data);
        rdata.removeCurrentSessionContent();
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_newRankingSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

    public IResponse openCreateContentFrontend(SessionRequestData rdata) {
        throw new CmsAuthorizationException();
    }

    //frontend
    public IResponse openEditContentFrontend(SessionRequestData rdata) {
        throw new CmsAuthorizationException();
    }

    //frontend
    public IResponse showEditContentFrontend(SessionRequestData rdata) {
        throw new CmsAuthorizationException();
    }

    //frontend
    public IResponse saveContentFrontend(SessionRequestData rdata) {
        throw new CmsAuthorizationException();
    }

    public IResponse cancelEditContentFrontend(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getCurrentSessionContent();
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.removeCurrentSessionContent();
        return show(rdata);
    }

    protected IResponse showEditContentData(ContentData contentData) {
        return new ForwardResponse(contentData.getContentDataJsp());
    }

    protected IResponse showEditRights(ContentData contentData) {
        return new ForwardResponse("/WEB-INF/_jsp/content/editGroupRights.ajax.jsp");
    }

    protected IResponse showSortChildContents() {
        return new ForwardResponse("/WEB-INF/_jsp/content/sortChildContents.ajax.jsp");
    }

    protected IResponse showContentAdministration(SessionRequestData rdata, int contentId) {
        return new ForwardResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
