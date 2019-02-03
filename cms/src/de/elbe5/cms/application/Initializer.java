/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.cache.StringCache;
import de.elbe5.base.data.Locales;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.configuration.ConfigurationBean;
import de.elbe5.cms.database.DbConnector;
import de.elbe5.cms.database.DbCreator;
import de.elbe5.cms.field.FieldActions;
import de.elbe5.cms.file.FileActions;
import de.elbe5.cms.file.FileCache;
import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.page.PageCache;
import de.elbe5.cms.page.PagePartActions;
import de.elbe5.cms.rights.CmsRightBean;
import de.elbe5.cms.rights.RightBean;
import de.elbe5.cms.rights.RightsCache;
import de.elbe5.cms.search.SearchActions;
import de.elbe5.cms.search.SearchIndexTask;
import de.elbe5.cms.template.TemplateActions;
import de.elbe5.cms.template.TemplateBean;
import de.elbe5.cms.timer.HeartbeatTaskData;
import de.elbe5.cms.timer.TimerController;
import de.elbe5.cms.user.UserActions;
import de.elbe5.cms.user.UserBean;

import java.util.List;

public abstract class Initializer {

    private static Initializer instance = null;

    public static void setInstance(Initializer instance) {
        Initializer.instance = instance;
    }

    public static Initializer getInstance() {
        return instance;
    }

    protected boolean databaseInstalled = false;
    private boolean systemPasswordSet = false;
    protected boolean initialized = false;

    public Initializer() {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        return false;
    }

    public void reset() {
    }

    public abstract String getDbName();

    public abstract List<String> getSqlScriptNames();

    public boolean isDatabaseInstalled() {
        if (databaseInstalled)
            return true;
        if (!DbConnector.getInstance().isInitialized() && !DbConnector.getInstance().loadDataSource(getDbName())) {
            return false;
        }
        databaseInstalled = DbCreator.getInstance().isDatabaseCreated() || DbCreator.getInstance().createDatabase(getSqlScriptNames());
        return databaseInstalled;
    }

    public boolean isDatabasePrepared() {
        if (systemPasswordSet)
            return true;
        try {
            systemPasswordSet = !UserBean.getInstance().isSystemPasswordEmpty();
        } catch (Exception e) {
            return false;
        }
        return systemPasswordSet;
    }

    public void initializeActions() {
        AdminActions.initialize();
        ApplicationActions.initialize();
        DefaultActions.initialize();
        InstallerActions.initialize();
        FieldActions.initialize();
        FileActions.initialize();
        PageActions.initialize();
        PagePartActions.initialize();
        SearchActions.initialize();
        TemplateActions.initialize();
        UserActions.initialize();
    }

    public void initializeConfig() {
        Configuration config = ConfigurationBean.getInstance().getConfiguration();
        Configuration.getInstance().loadAppConfiguration(config);
        Strings.ensureStrings();
        if (StringCache.hasLocale(Locales.getInstance().getDefaultLocale())){
            StringCache.DEFAULT_LOCALE=Locales.getInstance().getDefaultLocale();
        }
    }

    public void initializeCaches() {
        TemplateBean.getInstance().writeAllTemplateFiles();
        // tree
        FileCache.getInstance().load();
        PageCache.getInstance().load();
        // rights
        RightBean.setInstance(new CmsRightBean());
        RightsCache.getInstance().checkDirty();
    }

    public void initializeTimer() {
        //timer
        TimerController.getInstance().registerTimerTask(new HeartbeatTaskData());
        TimerController.getInstance().registerTimerTask(new SearchIndexTask());
        TimerController.getInstance().loadTasks();
        TimerController.getInstance().startThread();
        initialized = true;
    }

}
