package de.elbe5.template;

import de.elbe5.application.ApplicationPath;
import de.elbe5.companion.FileCompanion;
import de.elbe5.log.Log;

import java.io.File;
import java.util.*;

public class TemplateCache implements FileCompanion {

    private static TemplateCache instance = new TemplateCache();

    public static void setInstance(TemplateCache instance) {
        TemplateCache.instance = instance;
    }

    public static TemplateCache getInstance() {
        return instance;
    }

    public static String templateBasePath = ApplicationPath.getAppTemplatePath()+"/";

    private final Map<String, Map<String, Template>> templates = new HashMap<>();

    public void addType(String type){
        if (!templates.containsKey(type)){
            templates.put(type, new HashMap<>());
        }
    }

    public Template getTemplate(String type, String name){
        if (templates.containsKey(type)){
            return templates.get(type).get(name);
        }
        return null;
    }

    public List<Template> getTemplates(String type){
        if (templates.containsKey(type)){
            List<Template> list = new ArrayList<>(templates.get(type).values());
            list.sort(Comparator.comparing(o -> o.name));
            return list;
        }
        return new ArrayList<>();
    }

    public void load(){
        for (String type : templates.keySet()){
            templates.get(type).clear();
            File dir = new File(templateBasePath + type);
            if (dir.exists() && dir.isDirectory()){
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String code = readTextFile(f);
                        Template tpl = new Template(type, getFileNameWithoutExtension(f.getName()));
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
