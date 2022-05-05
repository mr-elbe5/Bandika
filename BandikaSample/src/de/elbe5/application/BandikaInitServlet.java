/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.LocalizedStrings;
import de.elbe5.base.JsonWebToken;
import de.elbe5.base.Log;
import de.elbe5.ckeditor.CkEditorController;
import de.elbe5.company.CompanyCache;
import de.elbe5.company.CompanyController;
import de.elbe5.content.*;
import de.elbe5.database.DbConnector;
import de.elbe5.page.LayoutPartBean;
import de.elbe5.page.LayoutPartData;
import de.elbe5.file.*;
import de.elbe5.group.GroupController;
import de.elbe5.layout.LayoutCache;
import de.elbe5.page.*;
import de.elbe5.serverpage.SPTagFactory;
import de.elbe5.serverpagetags.*;
import de.elbe5.servlet.InitServlet;
import de.elbe5.timer.CleanupTaskData;
import de.elbe5.timer.HeartbeatTaskData;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerController;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class BandikaInitServlet extends InitServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        System.out.println("initializing Bandika Application...");
        ServletContext context=servletConfig.getServletContext();
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(context), ApplicationPath.getCatalinaAppROOTDir(context));
        Configuration.setConfigs(context);
        LocalizedStrings.addBundle("bandika", Configuration.getLocale());
        LocalizedStrings.addBundle("content", Configuration.getLocale());
        LocalizedStrings.addBundle("cms", Configuration.getLocale());
        LocalizedStrings.addBundle("application", Configuration.getLocale());
        Log.initLog(ApplicationPath.getAppName());
        if (!DbConnector.getInstance().initialize("jdbc/bandika"))
            return;
        Configuration.setAppTitle("Bandika");
        JsonWebToken.createSecretKey(Configuration.getSalt());
        AdminController.register(new ContentAdminController());
        ContentController.register(new ContentController());
        DocumentController.register(new DocumentController());
        ImageController.register(new ImageController());
        MediaController.register(new MediaController());
        CompanyController.register(new CompanyController());
        GroupController.register(new GroupController());
        PageController.register(new PageController());
        CkEditorController.register(new CkEditorController());
        TimerController.register(new TimerController());
        UserController.register(new UserController());
        ContentFactory.addClassInfo(ContentData.class, ContentBean.getInstance());
        ContentFactory.addClassInfo(LinkData.class, LinkBean.getInstance());
        FileFactory.addDocumentClassInfo(DocumentData.class, null);
        FileFactory.addImageClassInfo(ImageData.class, ImageBean.getInstance());
        FileFactory.addMediaClassInfo(MediaData.class, null);
        ContentFactory.addClassInfo(PageData.class, PageBean.getInstance());
        PagePartFactory.addClassInfo(LayoutPartData.class, LayoutPartBean.getInstance(),true);
        LayoutCache.addType(PageData.LAYOUT_TYPE);
        LayoutCache.addType(PagePartData.LAYOUT_TYPE);
        ContentFactory.addDefaultType(PageData.class);
        FileFactory.addDefaultDocumentType(DocumentData.class);
        FileFactory.addDefaultImageType(ImageData.class);
        FileFactory.addDefaultMediaType(MediaData.class);
        SPTagFactory.addTagType(SPIfTag.TYPE, SPIfTag.class);
        SPTagFactory.addTagType(SPIncludeTag.TYPE, SPIncludeTag.class);
        SPTagFactory.addTagType(SPBreadcrumbTag.TYPE, SPBreadcrumbTag.class);
        SPTagFactory.addTagType(SPContentTag.TYPE, SPContentTag.class);
        SPTagFactory.addTagType(SPContentAdminTreeTag.TYPE, SPContentAdminTreeTag.class);
        SPTagFactory.addTagType(SPFooterTag.TYPE, SPFooterTag.class);
        SPTagFactory.addTagType(SPFormTag.TYPE, SPFormTag.class);
        SPTagFactory.addTagType(SPFormCheckTag.TYPE, SPFormCheckTag.class);
        SPTagFactory.addTagType(SPFormDateTag.TYPE, SPFormDateTag.class);
        SPTagFactory.addTagType(SPFormErrorTag.TYPE, SPFormErrorTag.class);
        SPTagFactory.addTagType(SPFormFileTag.TYPE, SPFormFileTag.class);
        SPTagFactory.addTagType(SPFormLineTag.TYPE, SPFormLineTag.class);
        SPTagFactory.addTagType(SPFormPasswordTag.TYPE, SPFormPasswordTag.class);
        SPTagFactory.addTagType(SPFormRadioTag.TYPE, SPFormRadioTag.class);
        SPTagFactory.addTagType(SPFormSelectTag.TYPE, SPFormSelectTag.class);
        SPTagFactory.addTagType(SPFormTextAreaTag.TYPE, SPFormTextAreaTag.class);
        SPTagFactory.addTagType(SPFormTextTag.TYPE, SPFormTextTag.class);
        SPTagFactory.addTagType(SPGroupListTag.TYPE, SPGroupListTag.class);
        SPTagFactory.addTagType(SPHtmlFieldTag.TYPE, SPHtmlFieldTag.class);
        SPTagFactory.addTagType(SPMainNavTag.TYPE, SPMainNavTag.class);
        SPTagFactory.addTagType(SPMessageTag.TYPE, SPMessageTag.class);
        SPTagFactory.addTagType(SPSectionTag.TYPE, SPSectionTag.class);
        SPTagFactory.addTagType(SPSysNavTag.TYPE, SPSysNavTag.class);
        SPTagFactory.addTagType(SPTextFieldTag.TYPE, SPTextFieldTag.class);
        SPTagFactory.addTagType(SPTimerListTag.TYPE, SPTimerListTag.class);
        SPTagFactory.addTagType(SPUserListTag.TYPE, SPUserListTag.class);

        SPTagFactory.addTagType(SPBreadcrumbTag.TYPE, SPBreadcrumbTag.class);
        SPTagFactory.addTagType(SPContentTag.TYPE, SPContentTag.class);
        SPTagFactory.addTagType(SPContentAdminTreeTag.TYPE, SPContentAdminTreeTag.class);
        SPTagFactory.addTagType(SPFooterTag.TYPE, SPFooterTag.class);
        SPTagFactory.addTagType(SPMainNavTag.TYPE, SPMainNavTag.class);
        SPTagFactory.addTagType(SPSysNavTag.TYPE, SPSysNavTag.class);


        ContentCache.load();
        ContentCentral.setInstance(new AppContentCentral());
        CompanyCache.load();
        UserCache.load();
        LayoutCache.load();
        if (!FileBean.getInstance().assertFileDirectory()){
            Log.error("could not create file directory");
        }
        Timer.getInstance().registerTimerTask(new HeartbeatTaskData());
        Timer.getInstance().registerTimerTask(new CleanupTaskData());
        Log.log("load tasks");
        Timer.getInstance().loadTasks();
        Timer.getInstance().startThread();
        Log.log("Bandika initialized");
    }

}
