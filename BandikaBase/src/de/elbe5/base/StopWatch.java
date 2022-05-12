package de.elbe5.base;

import java.util.HashMap;
import java.util.Map;

public class StopWatch {

    static Map<String, Long> watches = new HashMap<>();

    static public void start(String name){
        watches.put(name, System.currentTimeMillis());
    }

    static public void stop(String name){
        Log.log(name + ": " + (System.currentTimeMillis() - watches.get(name)));
    }

    static public void clear(){
        watches.clear();
    }
}
