package de.elbe5.template;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.FileHelper;
import de.elbe5.base.Log;

import java.io.File;
import java.util.*;

public class TemplateCache {

    public static String templateBasePath = ApplicationPath.getAppTemplatePath()+"/";

    static final Map<String, Map<String, Template>> templates = new HashMap<>();

    public static void addType(String type){
        if (!templates.containsKey(type)){
            templates.put(type, new HashMap<>());
        }
    }

    public static Template getTemplate(String type, String name){
        if (templates.containsKey(type)){
            return templates.get(type).get(name);
        }
        return null;
    }

    public static List<Template> getTemplates(String type){
        if (templates.containsKey(type)){
            List<Template> list = new ArrayList<>(templates.get(type).values());
            list.sort(Comparator.comparing(o -> o.name));
            return list;
        }
        return new ArrayList<>();
    }

    public static void load(){
        for (String type : templates.keySet()){
            File dir = new File(templateBasePath + type);
            if (dir.exists() && dir.isDirectory()){
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String code = FileHelper.readTextFile(f);
                        Template tpl = new Template(type, FileHelper.getFileNameWithoutExtension(f.getName()));
                        if (!code.isEmpty() && new TemplateParser(code, tpl).parse()) {
                            Log.info("adding template '" + tpl.getName() + "' of type " + type);
                            templates.get(type).put(tpl.getName(), tpl);
                        }
                    }
                }
            }
        }
    }

}
