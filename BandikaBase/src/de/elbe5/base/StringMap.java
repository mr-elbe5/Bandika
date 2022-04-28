package de.elbe5.base;

import java.util.HashMap;

public class StringMap extends HashMap<String, String> {

    public String getString(String key){
        String result = super.get(key);
        return result==null ? "..." : result;
    }

}
