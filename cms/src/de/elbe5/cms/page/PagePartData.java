/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.cms.field.*;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

public class PagePartData extends BaseIdData implements IRequestData, Comparable<PagePartData> {

    protected String name = "";
    protected String sectionName = "";
    protected int ranking = 0;
    protected String templateName;
    protected boolean editable = true;
    protected String flexClass = "";
    protected String cssClasses = "";
    protected String script = "";
    protected boolean dynamic = false;
    protected LocalDateTime publishDate = null;
    protected String publishedContent = "";
    protected Set<Integer> pageIds = null;

    protected Map<String, Field> fields = new HashMap<>();

    public PagePartData() {
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setTemplateName(data.getTemplateName());
        setEditable((data.isEditable()));
        setFlexClass(data.getFlexClass());
        setCssClasses(data.getCssClasses());
        setScript(data.getScript());
        getFields().clear();
        for (Field f : data.getFields().values()){
            try {
                getFields().put(f.getName(), (Field) f.clone());
            }
            catch (CloneNotSupportedException ignore){}
        }
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return name.compareTo(data.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getFlexClass() {
        return flexClass;
    }

    public void setFlexClass(String flexClass) {
        this.flexClass = flexClass;
    }

    public String getCssClasses() {
        return cssClasses;
    }

    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }

    public String getCss(boolean flex){
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

    public boolean isDynamic() {
        for (Field field : fields.values()) {
            if (field.isDynamic()) {
                return true;
            }
        }
        return false;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public boolean hasUnpublishedDraft(){
        return publishDate==null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished(){
        return publishDate!=null;
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

    public void setTemplateData(TemplateData data) {
        setTemplateName(data.getName());
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

    public void getNodeUsage(Set<Integer> list) {
        for (Field field : fields.values()) {
            field.getNodeUsage(list);
        }
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public void readRequestData(HttpServletRequest request, RequestError error) {
        for (Field field : getFields().values()) {
            field.readRequestData(request, error);
        }
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        setFlexClass(RequestReader.getString(request, "flexClass"));
        setCssClasses(RequestReader.getString(request, "cssClasses"));
        setScript(RequestReader.getString(request, "script"));
    }

    public void prepareCopy() {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
    }

    public Field getNewField(String type) {
        switch (type){
            case TextField.FIELDTYPE: return new TextField();
            case HtmlField.FIELDTYPE: return new HtmlField();
            case ScriptField.FIELDTYPE: return new ScriptField();
        }
        return null;
    }

}
