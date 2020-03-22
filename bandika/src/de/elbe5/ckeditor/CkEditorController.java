/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.ckeditor;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageBean;
import de.elbe5.file.ImageData;
import de.elbe5.page.PageBean;
import de.elbe5.page.PageData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

public class CkEditorController extends ContentController {

    public static final String KEY = "ckeditor";

    private static CkEditorController instance = null;

    public static void setInstance(CkEditorController instance) {
        CkEditorController.instance = instance;
    }

    public static CkEditorController getInstance() {
        return instance;
    }

    public static void register(CkEditorController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openLinkBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        return new UrlView("/WEB-INF/_jsp/ckeditor/browseLinks.jsp");
    }

    public IView openImageBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        return new UrlView("/WEB-INF/_jsp/ckeditor/browseImages.jsp");
    }

    public IView addImage(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(data.hasUserEditRight(rdata));
        ImageData image=new ImageData();
        image.setCreateValues(data,rdata);
        image.readSettingsRequestData(rdata);
        ImageBean.getInstance().saveFile(image,true);
        ContentCache.setDirty();
        rdata.put("imageId", Integer.toString(image.getId()));
        return new UrlView("/WEB-INF/_jsp/ckeditor/addImage.ajax.jsp");
    }


}
