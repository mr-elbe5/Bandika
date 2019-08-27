/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage.templatepagepart;

import de.elbe5.templatepage.PagePartData;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateData;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemplatePagePartData extends PagePartData {

    protected String templateName;
    protected String cssClasses = "";
    protected String script = "";
    protected LocalDateTime publishDate = null;
    protected String publishedContent = "";

    protected Map<String, Field> fields = new HashMap<>();

    public TemplatePagePartData() {
    }

    public String getJspPath() {
        return jspBasePath + "/templatepagepart";
    }

    public void cloneData(TemplatePagePartData data) {
        super.cloneData(data);
        setTemplateName(data.getTemplateName());
        setCssClasses(data.getCssClasses());
        setScript(data.getScript());
        getFields().clear();
        for (Field f : data.getFields().values()) {
            try {
                getFields().put(f.getName(), (Field) f.clone());
            } catch (CloneNotSupportedException ignore) {
            }
        }
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getPartInclude() {
        return TemplateData.getTemplateUrl(TemplateData.TYPE_PART_TEMPLATE, getTemplateName());
    }

    public String getEditPartInclude() {
        return TemplateData.getTemplateUrl(TemplateData.TYPE_PART_TEMPLATE, getTemplateName());
    }

    public String getEditTitle(Locale lcale) {
        return getTemplateName() + "(ID=" + getId() + ")";
    }

    public void setTemplateData(TemplateData data) {
        setTemplateName(data.getName());
    }

    public String getCssClasses() {
        return cssClasses;
    }

    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }

    public String getCss(boolean flex) {
        if (flex)
            return getFlexClass() + " " + getCssClasses();
        return getCssClasses();
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return publishDate != null;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public TextField ensureTextField(String name) {
        Field field = fields.get(name);
        if (field instanceof TextField)
            return (TextField) field;
        TextField textfield = new TextField();
        textfield.setName(name);
        textfield.setPagePartId(getId());
        fields.put(name, textfield);
        return textfield;
    }

    public HtmlField ensureHtmlField(String name) {
        Field field = fields.get(name);
        if (field instanceof HtmlField)
            return (HtmlField) field;
        HtmlField htmlfield = new HtmlField();
        htmlfield.setName(name);
        htmlfield.setPagePartId(getId());
        fields.put(name, htmlfield);
        return htmlfield;
    }

    public ScriptField ensureScriptField(String name) {
        Field field = fields.get(name);
        if (field instanceof ScriptField)
            return (ScriptField) field;
        ScriptField scriptField = new ScriptField();
        scriptField.setName(name);
        scriptField.setPagePartId(getId());
        fields.put(name, scriptField);
        return scriptField;
    }

    @Override
    public void setCreateValues(RequestData rdata) {
        setTemplateName(rdata.getString("template"));
    }

    @Override
    public void readRequestData(RequestData rdata) {
        for (Field field : getFields().values()) {
            field.readRequestData(rdata);
        }
    }

    public void readPagePartSettingsData(RequestData rdata) {
        super.readPagePartSettingsData(rdata);
        setCssClasses(rdata.getString("cssClasses"));
        setScript(rdata.getString("script"));
    }

    public Field getNewField(String type) {
        switch (type) {
            case TextField.FIELDTYPE:
                return new TextField();
            case HtmlField.FIELDTYPE:
                return new HtmlField();
            case ScriptField.FIELDTYPE:
                return new ScriptField();
        }
        return null;
    }

}
