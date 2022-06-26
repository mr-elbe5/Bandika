package de.elbe5.application;

import de.elbe5.companion.FileCompanion;
import de.elbe5.content.ContentCache;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserCache;
import org.json.JSONObject;

public class BandikaJsonData implements FileCompanion {

    private static BandikaJsonData instance = null;

    public static BandikaJsonData getInstance() {
        if (instance == null) {
            instance = new BandikaJsonData();
        }
        return instance;
    }

    public void dump(){
        JSONObject data = new JSONObject();
        data.put("content", ContentCache.getContentRoot().toJSONObject());
        data.put("users", UserCache.toJson());
        writeTextFile(ApplicationPath.getAppJsonFilePath(), data.toString(2));
    }

}
