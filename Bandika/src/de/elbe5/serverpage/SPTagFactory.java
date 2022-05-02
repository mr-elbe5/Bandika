package de.elbe5.serverpage;

import de.elbe5.serverpagetags.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SPTagFactory {

    static Map<String, Class<? extends SPTag>> tagClasses = new HashMap<>();

    static public void addTagType(String type, Class<? extends SPTag> cls){
        tagClasses.put(type, cls);
    }

    static public SPTag createTag(String type) {
        Class<? extends SPTag> cls = tagClasses.get(type);
        if (cls != null) {
            try {
                Constructor<? extends SPTag> ctr = cls.getDeclaredConstructor(String.class);;
                return ctr.newInstance(type);
            }
            catch (Exception e){
                System.out.println("could n ot create templete tag for type " + type);
            }
        }
        return new SPTag();
    }

}
