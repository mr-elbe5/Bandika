/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.cms.field.*;
import de.elbe5.cms.blog.BlogActions;
import de.elbe5.cms.calendar.CalendarActions;
import de.elbe5.cms.sharing.SharingActions;
import de.elbe5.webbase.application.DefaultActions;
import de.elbe5.webbase.application.Initializer;
import de.elbe5.base.log.Log;
import de.elbe5.cms.configuration.ConfigActions;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.configuration.ConfigurationBean;
import de.elbe5.cms.file.FileActions;
import de.elbe5.cms.file.PreviewCache;
import de.elbe5.cms.group.GroupActions;
import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.page.PagePartActions;
import de.elbe5.cms.rights.CmsRightBean;
import de.elbe5.cms.search.SearchActions;
import de.elbe5.cms.search.SearchIndexTask;
import de.elbe5.cms.site.SiteActions;
import de.elbe5.cms.template.TemplateActions;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.timer.HeartbeatTask;
import de.elbe5.cms.timer.TimerActions;
import de.elbe5.cms.timer.TimerController;
import de.elbe5.cms.tree.TreeActions;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.cms.user.UserActions;
import de.elbe5.webbase.rights.RightsCache;
import de.elbe5.webbase.user.LoginActions;

public class CmsInitializer extends Initializer {

    public CmsInitializer() {

    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        if (!initialized) {
            Log.log("initializing");
            // actions
            AdminActions.initialize();
            ApplicationActions.initialize();
            DynamicsActions.initialize();
            DefaultActions.initialize();
            InstallerActions.initialize();
            ConfigActions.initialize();
            FieldActions.initialize();
            FileActions.initialize();
            GroupActions.initialize();
            PageActions.initialize();
            PagePartActions.initialize();
            SearchActions.initialize();
            SiteActions.initialize();
            BlogActions.initialize();
            SharingActions.initialize();
            CalendarActions.initialize();
            TemplateActions.initialize();
            TimerActions.initialize();
            TreeActions.initialize();
            LoginActions.initialize();
            UserActions.initialize();
            // cms fields
            Fields.registerFieldType(HtmlField.FIELDTYPE_HTML, HtmlField.class);
            Fields.registerFieldType(ImageField.FIELDTYPE_IMAGE, ImageField.class);
            Fields.registerFieldType(TextField.FIELDTYPE_TEXT, TextField.class);
            Fields.registerFieldType(ScriptField.FIELDTYPE_SCRIPT, ScriptField.class);

            Configuration config = ConfigurationBean.getInstance().getConfiguration();
            Configuration.getInstance().loadAppConfiguration(config);
            TemplateCache.getInstance().initialize();
            //dynamics
            DynamicsCache.getInstance().load();
            // tree
            TreeCache.getInstance().load();
            // rights
            CmsRightBean.getInstance();
            RightsCache.getInstance().checkDirty();
            //previews
            PreviewCache.getInstance().initialize(PreviewCache.CACHEKEY, 100);
            //timer
            TimerController.getInstance().registerTimerTask(new HeartbeatTask());
            TimerController.getInstance().registerTimerTask(new SearchIndexTask());
            TimerController.getInstance().loadTasks();
            TimerController.getInstance().startThread();
            initialized = true;
        }
        return true;
    }

    public void resetCaches() {
        TemplateCache.getInstance().setDirty();
        TreeCache.getInstance().setDirty();
        PreviewCache.getInstance().setDirty();
    }

    public void resetTimers() {
        TimerController.getInstance().loadTasks();
        TimerController.getInstance().restartThread();
    }

}
