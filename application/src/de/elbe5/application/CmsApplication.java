/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.CmsClientController;
import de.elbe5.webserver.application.Application;
import de.elbe5.webserver.configuration.ConfigurationController;
import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.application.DefaultController;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.cms.field.*;
import de.elbe5.cms.file.FileController;
import de.elbe5.cms.page.PageController;
import de.elbe5.cms.page.PagePartController;
import de.elbe5.base.rights.RightsCache;
import de.elbe5.cms.site.SiteController;
import de.elbe5.cms.template.TemplateBean;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateController;
import de.elbe5.webserver.timer.TimerCache;
import de.elbe5.webserver.timer.TimerController;
import de.elbe5.webserver.tree.TreeRightsProvider;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.webserver.user.UserController;

public class CmsApplication extends Application {

    public static void setInstance() {
        setInstance(new CmsApplication());
    }

    public boolean initialize() {
        if (!initialized){
            Log.log("initializing");
            // string resources
            StringUtil.bundleName="app";
            // controllers
            DefaultController.setInstance(new DefaultController());
            ActionControllerCache.addController(DefaultController.getInstance());
            LoginController.setInstance(new LoginController());
            ActionControllerCache.addController(LoginController.getInstance());
            UserController.setInstance(new UserController());
            ActionControllerCache.addController(UserController.getInstance());
            CmsClientController.setInstance(new CmsClientController());
            ActionControllerCache.addController(CmsClientController.getInstance());
            FieldController.setInstance(new FieldController());
            ActionControllerCache.addController(FieldController.getInstance());
            FileController.setInstance(new FileController());
            ActionControllerCache.addController(FileController.getInstance());
            PageController.setInstance(new PageController());
            ActionControllerCache.addController(PageController.getInstance());
            PagePartController.setInstance(new PagePartController());
            ActionControllerCache.addController(PagePartController.getInstance());
            SiteController.setInstance(new SiteController());
            ActionControllerCache.addController(SiteController.getInstance());
            TimerController.setInstance(new TimerController());
            ActionControllerCache.addController(TimerController.getInstance());
            TemplateController.setInstance(new TemplateController());
            ActionControllerCache.addController(TemplateController.getInstance());
            // cms fields
            HtmlField.initialize();
            ImageField.initialize();
            ImageLinkField.initialize();
            LinkField.initialize();
            TextAreaField.initialize();
            TextLineField.initialize();
            TextLinkField.initialize();
            // templates
            TemplateBean.getInstance().importAllTemplates();
            ConfigurationController.getInstance().loadAppConfiguration();
            TemplateCache.getInstance().initialize();
            // tree
            CmsTreeCache.getInstance().load();
            // rights
            RightsCache.getInstance().checkDirty();
            RightsCache.getInstance().addRightsProvider(new GeneralRightsProvider());
            RightsCache.getInstance().addRightsProvider(new TreeRightsProvider());
            //timer
            TimerCache.getInstance().checkDirty();
            TimerCache.getInstance().startThread();
            //controller mapper
            CmsServlet.setControllerMapper(new CmsControllerMapper());
            initialized = true;
        }
        return true;
    }

}
