/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.data.Strings;
import de.elbe5.base.data.BaseData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;
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

    protected IResponse openJspPage(String jsp) {
        JspContentData contentData = new JspContentData();
        contentData.setJsp(jsp);
        return new ContentResponse(contentData);
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
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        ContentBean.getInstance().increaseViewCount(data.getId());
        return data.getDefaultView();
    }

    //frontend
    public IResponse show(String url, RequestData rdata) {
        ContentData data;
        data = ContentCache.getContent(url);
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        //Log.log("show: "+data.getClass().getSimpleName());
        ContentBean.getInstance().increaseViewCount(data.getId());
        return data.getDefaultView();
    }

    //backend
    public IResponse openCreateContentData(RequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        assert(parentData!=null);
        checkRights(parentData.hasUserEditRight(rdata));
        String type = rdata.getString("type");
        ContentData data = ContentFactory.getNewData(type);
        assert(data!=null);
        data.setCreateValues(parentData, rdata);
        data.setRanking(parentData.getChildren().size());
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditContentData(data);
    }

    //backend
    public IResponse openEditContentData(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditContentData(data);
    }

    //backend
    public IResponse saveContentData(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
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
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentSaved",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    //backend
    public IResponse openEditRights(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentCache.getContent(data.getId()), rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showEditRights(data);
    }

    //backend
    public IResponse saveRights(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
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
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_rightsSaved",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    //backend
    public IResponse cutContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentBean.getInstance().getContent(contentId);
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setClipboardData(ContentRequestKeys.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse copyContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData srcData = ContentBean.getInstance().getContent(contentId);
        assert(srcData!=null);
        checkRights(srcData.hasUserEditRight(rdata));
        ContentData data = ContentFactory.getNewData(srcData.getType());
        assert(data!=null);
        data.copyData(srcData, rdata);
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(ContentRequestKeys.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse pasteContent(RequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData data=rdata.getClipboardData(ContentRequestKeys.KEY_CONTENT,ContentData.class);
        if (data==null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        ContentData parent = ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        Set<Integer> parentIds=new HashSet<>();
        parent.collectParentIds(parentIds);
        if (parentIds.contains(data.getId())){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        data.setParentId(parentId);
        data.setParent(parent);
        data.generatePath();
        data.setChangerId(rdata.getUserId());
        ContentBean.getInstance().saveContent(data);
        rdata.clearClipboardData(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentPasted",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse deleteContent(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data=ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata)) ;
        if (contentId < BaseData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        int parentId = ContentCache.getParentContentId(contentId);
        ContentBean.getInstance().deleteContent(contentId);
        ContentCache.setDirty();
        rdata.put("contentId", Integer.toString(parentId));
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentDeleted",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,parentId);
    }

    //backend
    public IResponse openSortChildPages(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.setSessionObject(ContentRequestKeys.KEY_CONTENT, data);
        return showSortChildContents();
    }

    //backend
    public IResponse saveChildPageRanking(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
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
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_newRankingSaved",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

    public IResponse openCreateContentFrontend(RequestData rdata) {
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    //frontend
    public IResponse openEditContentFrontend(RequestData rdata) {
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    //frontend
    public IResponse showEditContentFrontend(RequestData rdata) {
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    //frontend
    public IResponse saveContentFrontend(RequestData rdata) {
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public IResponse cancelEditContentFrontend(RequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        rdata.removeSessionObject(ContentRequestKeys.KEY_CONTENT);
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

    protected IResponse showContentAdministration(RequestData rdata, int contentId) {
        return new ForwardResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
