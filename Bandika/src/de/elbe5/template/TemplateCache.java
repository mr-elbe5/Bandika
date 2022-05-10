package de.elbe5.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateCache {

    static final Map<String, Template> templates = new HashMap<>();

    public static Template getTemplate(String path){
        if (templates.containsKey(path)){
            return templates.get(path);
        }
        Template template = new Template(path);
        if (template.loadFromFile()){
            templates.put(path, template);
            return template;
        }
        return null;
    }

}
