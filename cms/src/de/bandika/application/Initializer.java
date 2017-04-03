/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.base.cache.BaseCache;
import de.bandika.blog.BlogAction;
import de.bandika.cluster.ClusterAction;
import de.bandika.cluster.ClusterManager;
import de.bandika.cluster.ClusterMessageProcessor;
import de.bandika.configuration.ConfigAction;
import de.bandika.doccenter.DocCenterAction;
import de.bandika.file.FileAction;
import de.bandika.file.PreviewCache;
import de.bandika.group.GroupAction;
import de.bandika.templatecontrol.*;
import de.bandika.page.*;
import de.bandika.pagepart.PagePartAction;
import de.bandika.search.SearchAction;
import de.bandika.site.SiteAction;
import de.bandika.timer.TimerAction;
import de.bandika.templatecontrol.KeywordsControl;
import de.bandika.base.log.Log;
import de.bandika.rights.RightsCache;
import de.bandika.field.*;
import de.bandika.template.*;
import de.bandika.tree.TreeAction;
import de.bandika.tree.TreeCache;
import de.bandika.configuration.Configuration;
import de.bandika.timer.TimerCache;
import de.bandika.user.LoginAction;
import de.bandika.user.UserAction;

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
            Fields.registerFieldType(TextField.FIELDTYPE_TEXT, TextField.class);
            Fields.registerFieldType(ScriptField.FIELDTYPE_SCRIPT, ScriptField.class);
            TemplateControls.addPageControlClass(MainMenuControl.KEY, MainMenuControl.class);
            TemplateControls.addPageControlClass(BreadcrumbControl.KEY, BreadcrumbControl.class);
            TemplateControls.addPageControlClass(TopNavControl.KEY, TopNavControl.class);
            TemplateControls.addPageControlClass(TopAdminNavControl.KEY, TopAdminNavControl.class);
            TemplateControls.addPageControlClass(MessageControl.KEY, MessageControl.class);
            TemplateControls.addPageControlClass(KeywordsControl.KEY, KeywordsControl.class);
            TemplateControls.addPageControlClass(LayerControl.KEY, LayerControl.class);

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
            if (ClusterManager.getInstance().isInCluster()) {
                ClusterMessageProcessor.getInstance().putListener(BaseCache.LISTENER_TYPE, TemplateCache.getInstance());
                ClusterMessageProcessor.getInstance().putListener(BaseCache.LISTENER_TYPE, TreeCache.getInstance());
                ClusterMessageProcessor.getInstance().putListener(BaseCache.LISTENER_TYPE, RightsCache.getInstance());
                ClusterMessageProcessor.getInstance().putListener(BaseCache.LISTENER_TYPE, PreviewCache.getInstance());
            }
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
        if (ClusterManager.getInstance().isInCluster()) {
            ClusterManager.getInstance().broadcastMessage(BaseCache.LISTENER_TYPE, BaseCache.EVENT_DIRTY, 0);
        }
    }

}
