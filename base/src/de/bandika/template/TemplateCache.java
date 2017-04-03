/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika._base.*;
import de.bandika.cluster.ClusterMessageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class TemplateCache extends BaseCache implements IChangeListener {

  public static final String CACHEKEY = "cache|template";

  private static TemplateCache instance = null;

  public static TemplateCache getInstance() {
    if (instance == null) {
      instance = new TemplateCache();
      instance.initialize();
    }
    return instance;
  }

  protected HashMap<String, ArrayList<TemplateData>> typeTemplates = new HashMap<String, ArrayList<TemplateData>>();
  protected HashMap<String, HashMap<String, Class>> typeClassMap = new HashMap<String, HashMap<String, Class>>();

  public String getCacheKey() {
    return CACHEKEY;
  }

  public void initialize() {
    checkDirty();
    ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
  }

  public void load() {
    typeTemplates.clear();
    typeClassMap.clear();
    Logger.info(null, "Initializing templates...");
    TemplateBean bean = TemplateBean.getInstance();
    ArrayList<TemplateTypeData> types = TemplateBean.getInstance().getAllTemplateTypes();
    for (TemplateTypeData typeData : types) {
      ArrayList<TemplateData> templates = bean.getAllTemplates(typeData.getName());
      HashMap<String, Class> classMap = new HashMap<String, Class>();
      for (TemplateData data : templates) {
        String className = data.getClassName();
        Class cls = Object.class;
        if (className != null && !className.equals("")) {
          try {
            cls = Class.forName(className);
          } catch (Exception e) {
            Logger.warn(getClass(), "could not load class " + className + "for template " + data.getName());
          }
        }
        classMap.put(data.getName(), cls);
        Logger.info(null, "adding " + data.getTypeName() + " template: " + data.getName());
      }
      typeTemplates.put(typeData.getName(), templates);
      typeClassMap.put(typeData.getName(), classMap);
    }
  }

  public ArrayList<TemplateData> getTemplates(String typeName) {
    return typeTemplates.get(typeName);
  }

  public ArrayList<TemplateData> getMatchingTemplates(String typeName, String matchTypes) {
    ArrayList<TemplateData> list = new ArrayList<TemplateData>(typeTemplates.get(typeName));
    if (StringHelper.isNullOrEmtpy(matchTypes))
      return list;
    ArrayList<String> matchList = new ArrayList<String>();
    StringTokenizer stk = new StringTokenizer(matchTypes, ",");
    while (stk.hasMoreTokens())
      matchList.add(stk.nextToken());
    for (int i = list.size() - 1; i >= 0; i--) {
      TemplateData data = list.get(i);
      if (StringHelper.isNullOrEmtpy(data.getMatchTypes()))
        continue;
      ArrayList<String> tplMatchList = data.getMatchTypeList();
      boolean matching = false;
      for (String tplMatch : tplMatchList) {
        if (matchList.contains(tplMatch)) {
          matching = true;
          break;
        }
      }
      if (!matching)
        list.remove(i);
    }
    return list;
  }

  public TemplateData getTemplate(String typeName, String name) {
    for (TemplateData data : typeTemplates.get(typeName)) {
      if (data.getName().equals(name))
        return data;
    }
    return null;
  }

  public BaseData getDataInstance(String typeName, String name) {
    checkDirty();
    if (!typeClassMap.containsKey(typeName))
      return null;
    HashMap<String, Class> map = typeClassMap.get(typeName);
    if (!map.containsKey(name))
      return null;
    BaseData template = null;
    try {
      template = (BaseData) map.get(name).newInstance();
    } catch (Exception ignore) {
    }
    return template;
  }

  public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
    Logger.info(getClass(), String.format("%s changed with action %s, id %s", messageKey, action, itemId));
    if (action.equals(IChangeListener.ACTION_SETDIRTY))
      setDirty();
  }

}
