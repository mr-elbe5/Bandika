/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageBean;
import de.elbe5.file.ImageData;
import de.elbe5.request.*;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;

public class PageController extends ContentController {

    public static final String KEY = "page";

    private static PageController instance = null;

    public static void setInstance(PageController instance) {
        PageController.instance = instance;
    }

    public static PageController getInstance() {
        return instance;
    }

    public static void register(PageController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    //frontend
    @Override
    public IResponse openEditContentFrontend(SessionRequestData rdata) {
        int contentId = rdata.getId();
        PageData data = ContentBean.getInstance().getContent(contentId,PageData.class);
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        data.setEditValues(ContentBean.getInstance().getContent(data.getId()), rdata);
        data.startEditing();
        rdata.setCurrentSessionContent(data);
        return data.getDefaultView();
    }

    //frontend
    @Override
    public IResponse showEditContentFrontend(SessionRequestData rdata) {
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        return data.getDefaultView();
    }

    //frontend
    @Override
    public IResponse saveContentFrontend(SessionRequestData rdata) {
        int contentId = rdata.getId();
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserEditRight(rdata));
        data.readFrontendRequestData(rdata);
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveContent(data)) {
            setSaveError(rdata);
            return data.getDefaultView();
        }
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        rdata.removeCurrentSessionContent();
        ContentCache.setDirty();
        return show(rdata);
    }

    //frontend
    @Override
    public IResponse cancelEditContentFrontend(SessionRequestData rdata) {
        int contentId = rdata.getId();
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        data.stopEditing();
        return data.getDefaultView();
    }

    public IResponse showDraft(SessionRequestData rdata){
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        return data.getDefaultView();
    }

    public IResponse showPublished(SessionRequestData rdata){
        int contentId = rdata.getId();
        ContentData data = ContentCache.getContent(contentId);
        assert(data!=null);
        checkRights(data.hasUserReadRight(rdata));
        data.setViewType(ContentData.VIEW_TYPE_SHOWPUBLISHED);
        return data.getDefaultView();
    }

    //frontend
    public IResponse publishPage(SessionRequestData rdata){
        int contentId = rdata.getId();
        PageData data=ContentBean.getInstance().getContent(contentId,PageData.class);
        assert(data != null);
        checkRights(data.hasUserApproveRight(rdata));
        data.setViewType(ContentData.VIEW_TYPE_PUBLISH);
        data.setPublishDate(PageBean.getInstance().getServerTime());
        return data.getDefaultView();
    }

    public IResponse openLinkBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        return new ForwardResponse("/WEB-INF/_jsp/ckeditor/browseLinks.jsp");
    }

    public IResponse openImageBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        return new ForwardResponse("/WEB-INF/_jsp/ckeditor/browseImages.jsp");
    }

    public IResponse addImage(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        ImageData image=new ImageData();
        image.setCreateValues(data,rdata);
        image.readSettingsRequestData(rdata);
        ImageBean.getInstance().saveFile(image,true);
        ContentCache.setDirty();
        rdata.put("imageId", Integer.toString(image.getId()));
        return new ForwardResponse("/WEB-INF/_jsp/ckeditor/addImage.ajax.jsp");
    }


}
