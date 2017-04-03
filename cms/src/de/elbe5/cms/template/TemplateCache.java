/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.data.BaseData;
import de.elbe5.base.log.Log;

import java.util.*;

public class TemplateCache extends BaseCache {
    public static final String CACHEKEY = "cache|template";
    private static TemplateCache instance = null;

    public static TemplateCache getInstance() {
        if (instance == null) {
            instance = new TemplateCache();
        }
        return instance;
    }

    protected List<TemplateData> masterTemplates = new ArrayList<>();
    protected List<TemplateData> pageTemplates = new ArrayList<>();
    protected List<TemplateData> partTemplates = new ArrayList<>();
    protected Map<String, String> templateNames=new HashMap<>();
    protected Map<String, Class> partClassMap = new HashMap<>();

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize(){
        checkDirty();
    }

    public void load() {
        masterTemplates.clear();
        pageTemplates.clear();
        partTemplates.clear();
        partClassMap.clear();
        templateNames.clear();
        Log.log("initializing templates...");
        TemplateBean bean = TemplateBean.getInstance();
        masterTemplates.addAll(bean.getAllTemplates(TemplateData.TYPE_MASTER));
        pageTemplates.addAll(bean.getAllTemplates(TemplateData.TYPE_PAGE));
        partTemplates.addAll(bean.getAllTemplates(TemplateData.TYPE_PART));
        for (TemplateData data : partTemplates) {
            String className = data.getClassName();
            Class cls = Object.class;
            if (className != null && !className.isEmpty()) {
                try {
                    cls = Class.forName(className);
                } catch (Exception e) {
                    Log.warn("could not load class " + className + "for template " + data.getFileName());
                }
            }
            partClassMap.put(data.getFileName(), cls);
        }
    }

    public List<TemplateData> getMasterTemplates() {
        checkDirty();
        return masterTemplates;
    }

    public List<TemplateData> getPageTemplates() {
        checkDirty();
        return pageTemplates;
    }

    public List<TemplateData> getPartTemplates() {
        checkDirty();
        return partTemplates;
    }

    public TemplateData getTemplate(String templateType, String templateName) {
        checkDirty();
        switch (templateType) {
            case TemplateData.TYPE_MASTER:
                for (TemplateData data : masterTemplates) {
                    if (data.getFileName().equals(templateName)) return data;
                }
                break;
            case TemplateData.TYPE_PAGE:
                for (TemplateData data : pageTemplates) {
                    if (data.getFileName().equals(templateName)) return data;
                }
                break;
            case TemplateData.TYPE_PART:
                for (TemplateData data : partTemplates) {
                    if (data.getFileName().equals(templateName)) return data;
                }
                break;
        }
        return null;
    }

    public BaseData getPartDataInstance(String templateName) {
        checkDirty();
        if (!partClassMap.containsKey(templateName)) return null;
        BaseData data = null;
        try {
            data = (BaseData) partClassMap.get(templateName).newInstance();
        } catch (Exception ignore) {
        }
        return data;
    }

    public List<TemplateData> getAreaPartTemplates(String areaType) {
        checkDirty();
        List<TemplateData> templates = new ArrayList<>();
        for (TemplateData partTemplate : getPartTemplates()) {
            if (partTemplate.hasUsage(areaType)) templates.add(partTemplate);
        }
        return templates;
    }
}
