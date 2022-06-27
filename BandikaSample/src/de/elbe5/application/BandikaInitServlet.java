/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.administration.ContentAdminController;
import de.elbe5.data.JsonDataCenter;
import de.elbe5.data.LocalizedStrings;
import de.elbe5.data.JsonWebToken;
import de.elbe5.log.Log;
import de.elbe5.ckeditor.CkEditorController;
import de.elbe5.company.CompanyCache;
import de.elbe5.company.CompanyController;
import de.elbe5.content.*;
import de.elbe5.database.DbConnector;
import de.elbe5.template.*;
import de.elbe5.page.TemplatePartBean;
import de.elbe5.file.*;
import de.elbe5.group.GroupController;
import de.elbe5.page.*;
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
        if (!ApplicationPath.initializePath(context)) {
            System.out.println("could not create directories, please check access rights");
            return;
        }
        Configuration.setConfigs(context);
        LocalizedStrings.addResourceBundle("bandika", Configuration.getLocale());
        LocalizedStrings.addResourceBundle("content", Configuration.getLocale());
        LocalizedStrings.addResourceBundle("cms", Configuration.getLocale());
        LocalizedStrings.addResourceBundle("application", Configuration.getLocale());
        LocalizedStrings.addResourceBundle("layout", Configuration.getLocale());
        Log.initLog(ApplicationPath.getAppName());
        if (!DbConnector.getInstance().initialize("jdbc/bandika"))
            return;
        Configuration.setAppTitle("Bandika");
        Configuration.setAppName("bandikasample");
        JsonWebToken.createSecretKey(Configuration.getSalt());
        ContentAdminController.register(new ContentAdminController());
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
        TemplateTagFactory.addTagType(AdminSystemNavTag.TYPE, AdminSystemNavTag.class);
        TemplateTagFactory.addTagType(AdminUserNavTag.TYPE, AdminUserNavTag.class);
        TemplateTagFactory.addTagType(AdminContentNavTag.TYPE, AdminContentNavTag.class);
        TemplateTagFactory.addTagType(AdminContentLogNavTag.TYPE, AdminContentLogNavTag.class);
        TemplateTagFactory.addTagType(TopAdminNavTag.TYPE, TopAdminNavTag.class);
        TemplateTagFactory.addTagType(TopContentNavTag.TYPE, TopContentNavTag.class);
        TemplateTagFactory.addTagType(TopUserNavTag.TYPE, TopUserNavTag.class);
        TemplateTagFactory.addTagType(MainNavTag.TYPE, MainNavTag.class);
        TemplateTagFactory.addTagType(BreadcrumbTag.TYPE, BreadcrumbTag.class);
        TemplateTagFactory.addTagType(ContentTag.TYPE, ContentTag.class);
        TemplateTagFactory.addTagType(FooterTag.TYPE, FooterTag.class);
        TemplateTagFactory.addTagType(SectionTag.TYPE, SectionTag.class);
        TemplateTagFactory.addTagType(PartTag.TYPE, PartTag.class);
        TemplateTagFactory.addTagType(TextFieldTag.TYPE, TextFieldTag.class);
        TemplateTagFactory.addTagType(HtmlFieldTag.TYPE, HtmlFieldTag.class);
        TemplateTagFactory.addTagType(ContactTag.TYPE, ContactTag.class);

        TemplateCache.getInstance().addType("admin");
        TemplateCache.getInstance().addType("master");
        TemplateCache.getInstance().addType("page");
        TemplateCache.getInstance().addType("part");

        ContentCache.load();
        ContentCentral.setInstance(new AppContentCentral());
        CompanyCache.load();
        UserCache.load();
        TemplateCache.getInstance().load();
        Timer.getInstance().registerTimerTask(new HeartbeatTaskData());
        Timer.getInstance().registerTimerTask(new CleanupTaskData());
        Log.log("load tasks");
        Timer.getInstance().loadTasks();
        Timer.getInstance().startThread();
        Log.log("Bandika initialized");
        JsonDataCenter.getInstance().addPackage("content", ContentCache.getContentRoot().toJSONObject());
        JsonDataCenter.getInstance().addPackage("users", UserCache.toJson());
        BandikaJsonData.getInstance().dump();
    }

}
