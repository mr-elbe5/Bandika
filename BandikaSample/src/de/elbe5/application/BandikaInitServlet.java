/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.Strings;
import de.elbe5.base.JsonWebToken;
import de.elbe5.base.Log;
import de.elbe5.ckeditor.CkEditorController;
import de.elbe5.company.CompanyCache;
import de.elbe5.company.CompanyController;
import de.elbe5.content.*;
import de.elbe5.database.DbConnector;
import de.elbe5.html.IfTag;
import de.elbe5.layout.*;
import de.elbe5.page.TemplatePartBean;
import de.elbe5.file.*;
import de.elbe5.group.GroupController;
import de.elbe5.page.*;
import de.elbe5.layout.TemplateTagFactory;
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
        Strings.addBundle("bandika", Configuration.getLocale());
        Strings.addBundle("content", Configuration.getLocale());
        Strings.addBundle("cms", Configuration.getLocale());
        Strings.addBundle("application", Configuration.getLocale());
        Strings.addBundle("layout", Configuration.getLocale());
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

        FileFactory.addDocumentClassInfo(DocumentData.class, null);
        FileFactory.addImageClassInfo(ImageData.class, ImageBean.getInstance());
        FileFactory.addMediaClassInfo(MediaData.class, null);

        ContentFactory.addClassInfo(ContentData.class, ContentBean.getInstance());
        ContentFactory.addClassInfo(LinkData.class, LinkBean.getInstance());
        ContentFactory.addClassInfo(PageData.class, PageBean.getInstance());
        ContentFactory.addDefaultType(PageData.class);

        FileFactory.addDefaultDocumentType(DocumentData.class);
        FileFactory.addDefaultImageType(ImageData.class);
        FileFactory.addDefaultMediaType(MediaData.class);

        PagePartFactory.addClassInfo(TemplatePartData.class, TemplatePartBean.getInstance(),true);

        TemplateTagFactory.addTagType(IfTag.TYPE, IfTag.class);
        TemplateTagFactory.addTagType(MessageTag.TYPE, MessageTag.class);
        TemplateTagFactory.addTagType(SysNavTag.TYPE, SysNavTag.class);
        TemplateTagFactory.addTagType(MainNavTag.TYPE, MainNavTag.class);
        TemplateTagFactory.addTagType(BreadcrumbTag.TYPE, BreadcrumbTag.class);
        TemplateTagFactory.addTagType(ContentTag.TYPE, ContentTag.class);
        TemplateTagFactory.addTagType(FooterTag.TYPE, FooterTag.class);
        TemplateTagFactory.addTagType(SectionTag.TYPE, SectionTag.class);
        TemplateTagFactory.addTagType(PartTag.TYPE, PartTag.class);
        TemplateTagFactory.addTagType(TextFieldTag.TYPE, TextFieldTag.class);
        TemplateTagFactory.addTagType(HtmlFieldTag.TYPE, HtmlFieldTag.class);
        TemplateTagFactory.addTagType(ContactTag.TYPE, ContactTag.class);

        TemplateCache.addType("master");
        TemplateCache.addType("page");
        TemplateCache.addType("part");

        ContentCache.load();
        ContentCentral.setInstance(new AppContentCentral());
        CompanyCache.load();
        UserCache.load();
        TemplateCache.load();
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
