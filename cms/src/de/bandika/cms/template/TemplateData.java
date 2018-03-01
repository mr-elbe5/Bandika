/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.data.BaseData;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TemplateData extends BaseData implements Serializable {

    public static final String TYPE_MASTER = "MASTER";
    public static final String TYPE_PAGE = "PAGE";
    public static final String TYPE_PART = "PART";
    public static final String TYPE_SNIPPET = "SNIPPET";

    public static final String SECTION_TYPES_ALL = "all";

    protected String type = "";
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String sectionTypes = "";
    protected String code = "";
    protected String parsedCode = "";

    protected List<TemplateInclude> templateIncludes = new ArrayList<>();

    public TemplateData() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected List<String> sectionTypeList = new ArrayList<>();

    public String getSectionTypes() {
        return sectionTypes;
    }

    public boolean hasSectionType(String sectionType) {
        return sectionTypeList.contains(SECTION_TYPES_ALL) || sectionTypeList.contains(sectionType);
    }

    public void setSectionTypes(String sectionTypes) {
        this.sectionTypes = sectionTypes;
        sectionTypeList.clear();
        String[] arr = sectionTypes.split(",");
        for (String sectionType : arr) {
            if (!sectionType.isEmpty()) {
                sectionTypeList.add(sectionType);
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setParsedCode(String parsedCode) {
        this.parsedCode = parsedCode;
    }

    public List<TemplateInclude> getTemplateIncludes() {
        return templateIncludes;
    }

    public void writeTemplate(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        int start=0;
        int end;
        String placeholder;
        for (int i =0; i< templateIncludes.size(); i++) {
            TemplateInclude templateInclude=templateIncludes.get(i);
            placeholder="{{"+i+"}}";
            end=parsedCode.indexOf(placeholder,start);
            if (end==-1) throw new IOException("missing placeholder");
            writer.write(parsedCode.substring(start,end));
            templateInclude.completeOutputData(outputData);
            if (templateInclude.isDynamic() && outputContext.getRequest()==null){
                writer.write(templateInclude.getPlaceholder());
            }
            else {
                templateInclude.writeHtml(outputContext, outputData);
            }
            start=end+placeholder.length();
        }
        writer.write(parsedCode.substring(start));
    }

}
