/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.cache.BaseCache;
import de.bandika.base.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateCache extends BaseCache {

    private static TemplateCache instance = null;

    public static TemplateCache getInstance() {
        if (instance == null) {
            instance = new TemplateCache();
        }
        return instance;
    }

    protected Map<String, List<TemplateData>> templates = new HashMap<>();

    public void initialize() {
        checkDirty();
    }

    @Override
    public void load() {
        Log.log("loading templates...");
        templates = TemplateBean.getInstance().getAllTemplates();
    }

    public List<TemplateData> getTemplates(String type) {
        checkDirty();
        return templates.get(type);
    }

    public TemplateData getTemplate(String type, String name) {
        for (TemplateData data : getTemplates(type)) {
            if (data.getName().equals(name))
                return data;
        }
        return null;
    }

    public List<TemplateData> getTemplates(String type, String parentType) {
        checkDirty();
        List<TemplateData> templates = new ArrayList<>();
        for (TemplateData template : getTemplates(type)) {
            if (template.hasSectionType(parentType)) {
                templates.add(template);
            }
        }
        return templates;
    }

}
