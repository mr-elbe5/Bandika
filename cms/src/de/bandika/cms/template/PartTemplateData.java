/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.field.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class PartTemplateData extends TemplateData {

    PartTemplateDataType dataType=PartTemplateDataType.DEFAULT;

    public PartTemplateData() {
        type = TemplateType.PART;
    }

    public PartTemplateDataType getDataType() {
        return dataType;
    }

    public String getDataTypeName(){
        return getDataType().name();
    }

    public void setDataTypeName(String dataTypeName){
        dataType=PartTemplateDataType.getPageTemplateDataType(dataTypeName);
    }

    protected boolean appendTagReplacement(StringBuilder sb, TagType tagType, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData, HttpServletRequest request) {
        if (super.appendTagReplacement(sb, tagType, attributes, content, pageData, partData, request))
            return true;
        switch (tagType) {
            case FIELD:
                appendField(sb, attributes, content, pageData, partData, request);
                return true;
            case PARTID:
                if (partData != null)
                    sb.append(partData.getHtmlId());
                return true;
        }
        return false;
    }

    protected void appendField(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData, HttpServletRequest request) {
        String fieldType = attributes.getString("type");
        String fieldName = attributes.getString("name");
        Field field = partData.ensureField(fieldName, fieldType);
        field.appendFieldHtml(sb, attributes, content, partData, pageData, request);

    }

    protected boolean appendTagReplacement(PageContext context, JspWriter writer, HttpServletRequest request, TagType tagType, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData) throws IOException {
        if (super.appendTagReplacement(context, writer, request, tagType, attributes, content, pageData, partData))
            return true;
        switch (tagType) {
            case FIELD:
                appendField(context, writer, request, attributes, content, pageData, partData);
                return true;
            case PARTID:
                if (partData != null)
                    writer.write(partData.getHtmlId());
                return true;
        }
        return false;
    }

    protected void appendField(PageContext context, JspWriter writer, HttpServletRequest request, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData) throws IOException {
        String fieldType = attributes.getString("type");
        String fieldName = attributes.getString("name");
        Field field = partData.ensureField(fieldName, fieldType);
        field.appendFieldHtml(context, writer, request, attributes, content, partData, pageData);

    }

}
