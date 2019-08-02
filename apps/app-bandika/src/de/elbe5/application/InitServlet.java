/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.cache.StringCache;
import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.log.Log;
import de.elbe5.cms.application.AdminController;
import de.elbe5.cms.application.ApplicationPath;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.database.DbConnector;
import de.elbe5.cms.file.FileCache;
import de.elbe5.cms.file.FileController;
import de.elbe5.cms.page.*;
import de.elbe5.cms.page.templatepage.*;
//import de.elbe5.cms.page.templatepage.contactpagepart.ContactPagePartBean;
//import de.elbe5.cms.page.templatepage.contactpagepart.ContactPagePartController;
//import de.elbe5.cms.page.templatepage.contactpagepart.ContactPagePartData;
//import de.elbe5.cms.page.templatepage.issuepagepart.IssuePagePartBean;
//import de.elbe5.cms.page.templatepage.issuepagepart.IssuePagePartController;
//import de.elbe5.cms.page.templatepage.issuepagepart.IssuePagePartData;
import de.elbe5.cms.page.templatepage.templatepagepart.TemplatePagePartBean;
import de.elbe5.cms.page.templatepage.templatepagepart.TemplatePagePartController;
import de.elbe5.cms.page.templatepage.templatepagepart.TemplatePagePartData;
//import de.elbe5.cms.page.templatepage.workflowpagepart.WorkflowPagePartBean;
//import de.elbe5.cms.page.templatepage.workflowpagepart.WorkflowPagePartController;
//import de.elbe5.cms.page.templatepage.workflowpagepart.WorkflowPagePartData;
import de.elbe5.cms.request.ResponseCode;
import de.elbe5.cms.rights.CmsRightBean;
import de.elbe5.cms.rights.RightBean;
import de.elbe5.cms.rights.RightsCache;
import de.elbe5.cms.search.SearchController;
import de.elbe5.cms.search.SearchIndexTask;
import de.elbe5.cms.servlet.ControllerCache;
import de.elbe5.cms.servlet.WebServlet;
import de.elbe5.cms.template.TemplateBean;
import de.elbe5.cms.template.TemplateController;
import de.elbe5.cms.timer.HeartbeatTaskData;
import de.elbe5.cms.timer.Timer;
import de.elbe5.cms.timer.TimerController;
import de.elbe5.cms.user.UserController;

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
        Strings.ensureStrings();
        if (StringCache.hasLocale(Configuration.getInstance().getDefaultLocale())) {
            StringCache.DEFAULT_LOCALE = Configuration.getInstance().getDefaultLocale();
        }
        ControllerCache.addController(AdminController.KEY,AdminController.getInstance());
        ControllerCache.addController(FileController.KEY,FileController.getInstance());
        ControllerCache.addController(PageController.KEY, PageController.getInstance());
        ControllerCache.addController(TemplatePageController.KEY,TemplatePageController.getInstance());
        ControllerCache.addController(TemplatePagePartController.KEY,TemplatePagePartController.getInstance());
        //ControllerCache.addController(ContactPagePartController.KEY,ContactPagePartController.getInstance());
        //ControllerCache.addController(IssuePagePartController.KEY,IssuePagePartController.getInstance());
        //ControllerCache.addController(WorkflowPagePartController.KEY,WorkflowPagePartController.getInstance());
        ControllerCache.addController(SearchController.KEY,SearchController.getInstance());
        ControllerCache.addController(TemplateController.KEY,TemplateController.getInstance());
        ControllerCache.addController(TimerController.KEY,TimerController.getInstance());
        ControllerCache.addController(UserController.KEY,UserController.getInstance());
        PageFactory.addInfo(new PageInfo(PageData.class, null));
        PageFactory.addInfo(new PageInfo(TemplatePageData.class, TemplatePageBean.getInstance()));
        PagePartFactory.addInfo(new PagePartInfo(TemplatePagePartData.class, TemplatePagePartBean.getInstance()));
        //PagePartFactory.addInfo(new PagePartInfo(ContactPagePartData.class, ContactPagePartBean.getInstance()));
        //PagePartFactory.addInfo(new PagePartInfo(IssuePagePartData.class, IssuePagePartBean.getInstance()));
        //PagePartFactory.addInfo(new PagePartInfo(WorkflowPagePartData.class, WorkflowPagePartBean.getInstance()));
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
