package de.elbe5.data;

import org.json.JSONObject;

public interface IJsonDataPackage {

    String getName();
    JSONObject saveAsJson();
    void loadFromJson(JSONObject jsonObject);

}
