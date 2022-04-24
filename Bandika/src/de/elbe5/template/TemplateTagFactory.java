package de.elbe5.template;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TemplateTagFactory {

    static Map<String, Class<? extends TemplateTag>> tagClasses = new HashMap<>();

    static void addTemplateType(String type, Class<? extends TemplateTag> cls){
        tagClasses.put(type, cls);
    }

    static public TemplateTag createTag(String type) {
        Class<? extends TemplateTag> cls = tagClasses.get(type);
        if (cls != null) {
            try {
                Constructor<? extends TemplateTag> ctr = cls.getDeclaredConstructor(String.class);
                return ctr.newInstance(type);
            }
            catch (Exception e){
                System.out.println("could n ot create templete tag for type " + type);
            }
        }
        return new TemplateTag(type);
    }

}
