/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.log.Log;
import de.elbe5.configuration.Configuration;
import de.elbe5.database.DbConnector;
import de.elbe5.file.FileCache;
import de.elbe5.file.FileController;
import de.elbe5.page.*;
//import de.elbe5.page.templatepage.contactpagepart.ContactPagePartBean;
//import de.elbe5.page.templatepage.contactpagepart.ContactPagePartController;
//import de.elbe5.page.templatepage.contactpagepart.ContactPagePartData;
import de.elbe5.template.TemplateFactory;
import de.elbe5.template.TemplateInfo;
import de.elbe5.templatepage.*;
import de.elbe5.templatepage.templatepagepart.TemplatePagePartBean;
import de.elbe5.templatepage.templatepagepart.TemplatePagePartController;
import de.elbe5.templatepage.templatepagepart.TemplatePagePartData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.CmsRightBean;
import de.elbe5.rights.RightBean;
import de.elbe5.rights.RightsCache;
import de.elbe5.search.SearchController;
import de.elbe5.search.SearchIndexTask;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.servlet.WebServlet;
import de.elbe5.template.TemplateBean;
import de.elbe5.template.TemplateController;
import de.elbe5.timer.HeartbeatTaskData;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerController;
import de.elbe5.user.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InitServlet extends WebServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("initializing Bandika Application...");
        super.init(servletConfig);
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(getServletContext()), ApplicationPath.getCatalinaAppROOTDir(getServletContext()));
        Configuration.getInstance().setConfigs(servletConfig.getServletContext());
        Log.initLog(ApplicationPath.getAppName());
        if (!DbConnector.getInstance().initialize("jdbc/bandika"))
            return;
        Configuration.getInstance().setAppTitle("Bandika");
        Configuration.getInstance().loadLocales();
        Strings.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/webserver-strings.csv");
        Strings.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/templatepage-strings.csv");
        Strings.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/application-strings.csv");
        if (Strings.hasLocale(Configuration.getInstance().getDefaultLocale())) {
            Strings.DEFAULT_LOCALE = Configuration.getInstance().getDefaultLocale();
        }
        ControllerCache.addController(AdminController.KEY,AdminController.getInstance());
        ControllerCache.addController(FileController.KEY,FileController.getInstance());
        ControllerCache.addController(PageController.KEY, PageController.getInstance());
        ControllerCache.addController(TemplatePageController.KEY,TemplatePageController.getInstance());
        ControllerCache.addController(TemplatePagePartController.KEY,TemplatePagePartController.getInstance());
        //ControllerCache.addController(ContactPagePartController.KEY,ContactPagePartController.getInstance());
        //ControllerCache.addController(IssuetrackerPageController.KEY,IssuePagePartController.getInstance());
        ControllerCache.addController(SearchController.KEY,SearchController.getInstance());
        ControllerCache.addController(TemplateController.KEY,TemplateController.getInstance());
        ControllerCache.addController(TimerController.KEY,TimerController.getInstance());
        ControllerCache.addController(UserController.KEY,UserController.getInstance());
        PageFactory.addInfo(new PageInfo(PageData.class, null));
        PageFactory.addInfo(new PageInfo(TemplatePageData.class, TemplatePageBean.getInstance()));
        PagePartFactory.addInfo(new PagePartInfo(TemplatePagePartData.class, TemplatePagePartBean.getInstance()));
        //PagePartFactory.addInfo(new PagePartInfo(ContactPagePartData.class, ContactPagePartBean.getInstance()));
        TemplateFactory.addInfo(new TemplateInfo(PageData.TYPE_MASTER_TEMPLATE, "_masterTemplates", new String[]{"cms"}));
        TemplateFactory.addInfo(new TemplateInfo(PageData.TYPE_PAGE_TEMPLATE, "_pageTemplates", new String[]{"cms","cmspart"}));
        TemplateFactory.addInfo(new TemplateInfo(TemplatePageData.TYPE_PART_TEMPLATE, "_partTemplates", new String[]{"cms","cmspart"}));
        TemplateBean.getInstance().writeAllTemplateFiles();
        FileCache.getInstance().load();
        PageCache.getInstance().load();
        RightBean.setInstance(new CmsRightBean());
        RightsCache.getInstance().checkDirty();
        Timer.getInstance().registerTimerTask(new HeartbeatTaskData());
        Timer.getInstance().registerTimerTask(new SearchIndexTask());
        Timer.getInstance().loadTasks();
        Timer.getInstance().startThread();
        Log.log("Bandika initialized");
        //generatePassword();
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(ResponseCode.NOT_FOUND);
    }

    private void generatePassword() {
        try {
            String salt = PBKDF2Encryption.generateSaltBase64();
            String password = PBKDF2Encryption.getEncryptedPasswordBase64("pass", salt);
            Log.info("salt= " + salt + " password= " + password);
        } catch (Exception e) {
            Log.warn("password generation failed");
        }
    }
}
