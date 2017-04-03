/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.blog.BlogAction;
import de.elbe5.cluster.ClusterAction;
import de.elbe5.cluster.ClusterManager;
import de.elbe5.configuration.ConfigAction;
import de.elbe5.doccenter.DocCenterAction;
import de.elbe5.file.FileAction;
import de.elbe5.file.PreviewCache;
import de.elbe5.group.GroupAction;
import de.elbe5.master.*;
import de.elbe5.page.*;
import de.elbe5.pagepart.PagePartAction;
import de.elbe5.search.SearchAction;
import de.elbe5.site.SiteAction;
import de.elbe5.timer.TimerAction;
import de.elbe5.tree.KeywordsControl;
import de.elbe5.base.log.Log;
import de.elbe5.rights.RightsCache;
import de.elbe5.field.*;
import de.elbe5.template.*;
import de.elbe5.tree.TreeAction;
import de.elbe5.tree.TreeCache;
import de.elbe5.configuration.Configuration;
import de.elbe5.timer.TimerCache;
import de.elbe5.user.LoginAction;
import de.elbe5.user.UserAction;

public class Initializer {

    private static Initializer instance = null;

    public static void setInstance(Initializer instance) {
        Initializer.instance = instance;
    }

    public static Initializer getInstance() {
        return instance;
    }

    protected boolean initialized = false;

    public Initializer() {

    }

    public void ensureStrings() {
        // string resources

    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        if (!initialized) {
            Log.log("initializing");
            // actions
            AdminAction.initialize();
            BlogAction.initialize();
            DefaultAction.initialize();
            DocCenterAction.initialize();
            InstallerAction.initialize();
            ClusterAction.initialize();
            ConfigAction.initialize();
            FieldAction.initialize();
            FileAction.initialize();
            GroupAction.initialize();
            PageAction.initialize();
            PagePartAction.initialize();
            SearchAction.initialize();
            SiteAction.initialize();
            TemplateAction.initialize();
            TimerAction.initialize();
            TreeAction.initialize();
            LoginAction.initialize();
            UserAction.initialize();
            // controllers
            ClusterManager.setInstance(new ClusterManager());
            // cms fields
            Fields.registerFieldType(HtmlField.FIELDTYPE_HTML, HtmlField.class);
            Fields.registerFieldType(TextLineField.FIELDTYPE_TEXTLINE, TextLineField.class);
            Fields.registerFieldType(ScriptField.FIELDTYPE_SCRIPT, ScriptField.class);
            TemplateControls.addPageControlClass("mainMenu", MainMenuControl.class);
            TemplateControls.addPageControlClass("breadcrumb", BreadcrumbControl.class);
            TemplateControls.addPageControlClass("topNav", TopNavControl.class);
            TemplateControls.addPageControlClass("topAdminNav", TopAdminNavControl.class);
            TemplateControls.addPageControlClass("message", MessageControl.class);
            TemplateControls.addPageControlClass("keywords", KeywordsControl.class);
            TemplateControls.addPageControlClass("layer", LayerControl.class);

            Configuration.getInstance().loadAppConfiguration();
            TemplateCache.getInstance().initialize();
            // tree
            TreeCache.getInstance().load();
            // treeRights
            RightsCache.getInstance().checkDirty();
            //previews
            PreviewCache.getInstance().initialize(PreviewCache.CACHEKEY, 100);
            // cluster
            ClusterManager.getInstance().initialize();
            //timer
            TimerCache.getInstance().checkDirty();
            TimerCache.getInstance().startThread();
            initialized = true;
        }
        return true;
    }

    public void resetCaches(){
        TemplateCache.getInstance().setDirty();
        TreeCache.getInstance().setDirty();
        PreviewCache.getInstance().setDirty();
        ClusterManager.getInstance().initialize();
        TimerCache.getInstance().setDirty();
    }

}
