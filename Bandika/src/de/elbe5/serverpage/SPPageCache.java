package de.elbe5.serverpage;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.FileHelper;

import java.util.HashMap;
import java.util.Map;

public class SPPageCache {

    static final Map<String, ServerPage> pages = new HashMap<>();

    public static ServerPage getPage(String path){
        if (pages.containsKey(path)){
            return pages.get(path);
        }
        ServerPage page = new ServerPage(path);
        if (page.loadFromFile()){
            pages.put(path, page);
            return page;
        }
        return null;
    }

}
