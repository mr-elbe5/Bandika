/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika.data.*;

import java.util.*;

public class TemplateCache extends BaseCache implements IChangeListener {

    public static final String CACHEKEY = "cache|template";

    private static TemplateCache instance = null;

    public static TemplateCache getInstance() {
        if (instance == null) {
            instance = new TemplateCache();
        }
        return instance;
    }

    protected List<MasterTemplateData> masterTemplates = new ArrayList<>();
    protected Map<String, Class> masterClassMap = new HashMap<>();

    protected List<LayoutTemplateData> layoutTemplates = new ArrayList<>();
    protected Map<String, Class> layoutClassMap = new HashMap<>();

    protected List<PartTemplateData> partTemplates = new ArrayList<>();
    protected Map<String, Class> partClassMap = new HashMap<>();

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        checkDirty();
        //todo
        //ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
    }

    public void load() {
        masterTemplates.clear();
        masterClassMap.clear();
        layoutTemplates.clear();
        layoutClassMap.clear();
        partTemplates.clear();
        partClassMap.clear();
        Log.log("Initializing templates...");
        TemplateBean bean = TemplateBean.getInstance();
        masterTemplates.addAll(bean.getAllMasterTemplates());
        layoutTemplates.addAll(bean.getAllLayoutTemplates());
        partTemplates.addAll(bean.getAllPartTemplates());
        for (LayoutTemplateData data : layoutTemplates) {
            String className = data.getClassName();
            Class cls = Object.class;
            if (className != null && !className.equals("")) {
                try {
                    cls = Class.forName(className);
                } catch (Exception e) {
                    Log.warn("could not load class " + className + "for template " + data.getName());
                }
            }
            layoutClassMap.put(data.getName(), cls);
        }
        for (PartTemplateData data : partTemplates) {
            String className = data.getClassName();
            Class cls = Object.class;
            if (className != null && !className.equals("")) {
                try {
                    cls = Class.forName(className);
                } catch (Exception e) {
                    Log.warn("could not load class " + className + "for template " + data.getName());
                }
            }
            partClassMap.put(data.getName(), cls);
        }
    }

    public List<MasterTemplateData> getMasterTemplates() {
        return masterTemplates;
    }

    public List<LayoutTemplateData> getLayoutTemplates() {
        return layoutTemplates;
    }

    public List<PartTemplateData> getPartTemplates() {
        return partTemplates;
    }

    public MasterTemplateData getMasterTemplate(String name) {
        for (MasterTemplateData data : masterTemplates) {
            if (data.getName().equals(name))
                return data;
        }
        return null;
    }

    public LayoutTemplateData getLayoutTemplate(String name) {
        for (LayoutTemplateData data : layoutTemplates) {
            if (data.getName().equals(name))
                return data;
        }
        return null;
    }

    public PartTemplateData getPartTemplate(String name) {
        for (PartTemplateData data : partTemplates) {
            if (data.getName().equals(name))
                return data;
        }
        return null;
    }

    public BaseData getLayoutDataInstance(String name) {
        checkDirty();
        if (!layoutClassMap.containsKey(name))
            return null;
        BaseData data = null;
        try {
            data = (BaseData) layoutClassMap.get(name).newInstance();
        } catch (Exception ignore) {
        }
        return data;
    }

    public BaseData getPartDataInstance(String name) {
        checkDirty();
        if (!partClassMap.containsKey(name))
            return null;
        BaseData data = null;
        try {
            data = (BaseData) partClassMap.get(name).newInstance();
        } catch (Exception ignore) {
        }
        return data;
    }

    public List<PartTemplateData> getAreaPartTemplates(String areaType){
        List<PartTemplateData> templates=new ArrayList<>();
        for (PartTemplateData partTemplate : getPartTemplates()){
            if (partTemplate.hasAreaType(areaType))
                templates.add(partTemplate);
        }
        return templates;
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }

}
