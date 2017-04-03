/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import de.elbe5.base.cache.BaseCache;
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

    protected Map<TemplateType,List<TemplateData>> templates = new HashMap<>();

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        checkDirty();
    }

    @Override
    public void load() {
        Log.log("loading templates...");
        templates=TemplateBean.getInstance().getAllTemplates();
    }

    public List<TemplateData> getTemplates(TemplateType type) {
        checkDirty();
        return templates.get(type);
    }

    public TemplateData getTemplate(TemplateType type, String name){
        for (TemplateData data : getTemplates(type)){
            if (data.getName().equals(name))
                return data;
        }
        return null;
    }

    public List<TemplateData> getTemplates(TemplateType type, String usage) {
        checkDirty();
        List<TemplateData> templates = new ArrayList<>();
        for (TemplateData template : getTemplates(type)) {
            if (template.hasUsage(usage)) {
                templates.add(template);
            }
        }
        return templates;
    }


}
