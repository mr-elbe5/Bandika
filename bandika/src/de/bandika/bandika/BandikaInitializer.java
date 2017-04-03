package de.bandika.bandika;

import de.bandika.application.AppConfiguration;
import de.bandika.application.ApplicationBean;
import de.bandika.application.ApplicationController;
import de.bandika.application.GeneralRightsProvider;
import de.bandika.cluster.ClusterController;
import de.bandika.cms.*;
import de.bandika.data.ControllerCache;
import de.bandika.data.Log;
import de.bandika.data.StringCache;
import de.bandika.file.*;
import de.bandika.menu.MenuCache;
import de.bandika.page.PageCache;
import de.bandika.page.PageController;
import de.bandika.page.PageRightsProvider;
import de.bandika.rights.RightsCache;
import de.bandika.search.SearchController;
import de.bandika.search.SearchQueue;
import de.bandika.team.blog.TeamBlogController;
import de.bandika.team.chat.TeamChatController;
import de.bandika.team.file.TeamFileController;
import de.bandika.template.TemplateCache;
import de.bandika.template.TemplateController;
import de.bandika.timer.TimerController;
import de.bandika.user.UserController;

import java.util.Locale;

public class BandikaInitializer {

    public static void init() {
        AppConfiguration.getInstance().clear();
        AppConfiguration.getInstance().putAll(ApplicationBean.getInstance().getConfiguration());
        AppConfiguration.getInstance().setLocales(ApplicationBean.getInstance().getLocales());
        for (Locale locale : AppConfiguration.getInstance().getLocales())
            Log.log("found locale: " + locale.getLanguage() + "(" + locale.getDisplayName() + ")");
        Log.log("loading strings...");
        initStrings();
        Log.log("initializing controllers...");
        initController();
        Log.log("initializing caches...");
        initCaches();
        Log.log("initializing cms fields...");
        initFields();
        Log.log("initializing search queue...");
        SearchQueue.getInstance();
    }

    private static void initController() {
        //mod_portal
        ApplicationController.setInstance(new BandikaApplicationController());
        ControllerCache.addController(ApplicationController.getInstance());
        DocumentController.setInstance(new BandikaDocumentController());
        ControllerCache.addController(DocumentController.getInstance());
        ImageController.setInstance(new BandikaImageController());
        ControllerCache.addController(ImageController.getInstance());
        PageController.setInstance(new BandikaPageController());
        ControllerCache.addController(PageController.getInstance());
        TemplateController.setInstance(new BandikaTemplateController());
        ControllerCache.addController(TemplateController.getInstance());
        TimerController.setInstance(new BandikaTimerController());
        ControllerCache.addController(TimerController.getInstance());
        UserController.setInstance(new BandikaUserController());
        ControllerCache.addController(UserController.getInstance());
        //mod_cluster
        ClusterController.setInstance(new BandikaClusterController());
        ControllerCache.addController(ClusterController.getInstance());
        //mod_search
        SearchController.setInstance(new BandikaSearchController());
        ControllerCache.addController(SearchController.getInstance());
        //mod_team
        TeamBlogController.setInstance(new BandikaTeamBlogController());
        ControllerCache.addController(TeamBlogController.getInstance());
        TeamChatController.setInstance(new BandikaTeamChatController());
        ControllerCache.addController(TeamChatController.getInstance());
        TeamFileController.setInstance(new BandikaTeamFileController());
        ControllerCache.addController(TeamFileController.getInstance());

        ClusterController.getInstance().initialize();
        TimerController.getInstance().initialize();
    }

    private static void initStrings() {
        StringCache.initialize();
        StringCache.loadBundle("base_app");
        StringCache.loadBundle("mod_webapp");
        StringCache.loadBundle("mod_webuser");
        StringCache.loadBundle("mod_portal");
        StringCache.loadBundle("mod_cluster");
        StringCache.loadBundle("mod_cms");
        StringCache.loadBundle("mod_search");
        StringCache.loadBundle("mod_team");
        StringCache.loadBundle("bandika");
    }

    private static void initCaches() {
        DocumentCache.getInstance().initialize();
        ImageCache.getInstance().initialize();
        ThumbnailCache.getInstance().initialize();
        MenuCache.getInstance().initialize();
        PageCache.getInstance().initialize();
        RightsCache.getInstance().initialize();
        RightsCache.getInstance().addRightsProvider(new PageRightsProvider());
        RightsCache.getInstance().addRightsProvider(new GeneralRightsProvider());
        TemplateCache.getInstance().initialize();
    }

    private static void initFields() {
        TextLineField.initialize();
        TextAreaField.initialize();
        HtmlField.initialize();
        ImageField.initialize();
        LinkField.initialize();
        TextLinkField.initialize();
        ImageLinkField.initialize();
    }

}
