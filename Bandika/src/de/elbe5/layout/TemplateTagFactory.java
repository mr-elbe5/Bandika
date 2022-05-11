package de.elbe5.layout;

import de.elbe5.base.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TemplateTagFactory {

    static Map<String, Class<? extends TemplateTag>> tagClasses = new HashMap<>();

    static public void addTagType(String type, Class<? extends TemplateTag> cls){
        tagClasses.put(type, cls);
    }

    static public TemplateTag createTag(String type) {
        Class<? extends TemplateTag> cls = tagClasses.get(type);
        if (cls != null) {
            try {
                Constructor<? extends TemplateTag> ctr = cls.getDeclaredConstructor();
                return ctr.newInstance();
            }
            catch (Exception e){
                Log.error("could not create tag for type " + type);
            }
        }
        return null;
    }

}
