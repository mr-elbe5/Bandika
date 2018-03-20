/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;
import de.elbe5.cms.template.control.*;

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

    public static TemplateInclude getTemplateInclude(String type){
        if (type.isEmpty()) {
            Log.warn("element without type: "+type);
            return null;
        }
        switch (type){
            /* not parameterized return instance */
            case HeadInclude.KEY :
                return HeadInclude.getInstance();
            case TopNavControl.KEY :
                return TopNavControl.getInstance();
            case MainMenuControl.KEY :
                return MainMenuControl.getInstance();
            case BreadcrumbControl.KEY :
                return BreadcrumbControl.getInstance();
            case MessageControl.KEY :
                return MessageControl.getInstance();
            case PageContentControl.KEY :
                return PageContentControl.getInstance();
            case LayerControl.KEY :
                return LayerControl.getInstance();
            case SubNaviControl.KEY :
                return SubNaviControl.getInstance();
            /* parameterized return new */
            case FileListControl.KEY :
                return new FileListControl();
            case SectionInclude.KEY :
                return new SectionInclude();
            case FieldInclude.KEY :
                return new FieldInclude();
            case JspControl.KEY :
                return new JspControl();
            case ResourceInclude.KEY :
                return new ResourceInclude();
            case SnippetInclude.KEY :
                return new SnippetInclude();
            case DocumentsControl.KEY :
                return new DocumentsControl();
            case BlogControl.KEY :
                return new BlogControl();
            case CalendarControl.KEY :
                return new CalendarControl();
        }
        Log.warn("element without valid type: "+type);
        return null;
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

    public List<TemplateData> getTemplates(String type, String sectionType) {
        checkDirty();
        List<TemplateData> templates = new ArrayList<>();
        for (TemplateData template : getTemplates(type)) {
            if (template.hasSectionType(sectionType)) {
                templates.add(template);
            }
        }
        return templates;
    }

}
