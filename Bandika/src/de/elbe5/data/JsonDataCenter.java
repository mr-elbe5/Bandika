package de.elbe5.data;

import de.elbe5.application.ApplicationPath;
import de.elbe5.companion.FileCompanion;
import de.elbe5.log.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonDataCenter implements FileCompanion {

    private static final JsonDataCenter instance = new JsonDataCenter();

    public static JsonDataCenter getInstance() {
        return instance;
    }

    final Map<String, IJsonDataPackage> packages = new HashMap<>();

    public void addPackage(String name, IJsonDataPackage pack){
        packages.put(pack.getName(), pack);
    }

    public IJsonDataPackage getPackage(String name){
        return packages.get(name);
    }

    public void read(){
        packages.clear();
        String json = readTextFile(ApplicationPath.getAppJsonFilePath());
        JSONObject jsonObject = new JSONObject(json);
        for (String key : packages.keySet()){
            JSONObject data = jsonObject.optJSONObject(key);
            if (data != null){
                packages.get(key).loadFromJson(data);
            }
        }
    }

    public void dump(){
        JSONObject data = new JSONObject();
        for (String key : packages.keySet()){
            IJsonDataPackage pack = packages.get(key);
            data.put(key, packages.get(key).saveAsJson());
        }
        writeTextFile(ApplicationPath.getAppJsonFilePath(), data.toString(2));
    }

    public void load(){
        Log.log("loading");
        read();
    }

}
