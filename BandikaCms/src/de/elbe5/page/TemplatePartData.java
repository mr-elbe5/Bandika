/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;
import de.elbe5.template.Template;
import de.elbe5.template.TemplateCache;
import de.elbe5.request.RequestData;

import java.util.HashMap;
import java.util.Map;

@JsonClass
public class TemplatePartData extends PagePartData {

    @JsonField
    protected String templateName = "";

    @JsonField
    protected Map<String, PartField> fields = new HashMap<>();

    public TemplatePartData() {
    }

    public void copyData(PagePartData data) {
        super.copyData(data);
        if (!(data instanceof TemplatePartData))
            return;
        TemplatePartData tpdata=(TemplatePartData)data;
        setTemplateName(tpdata.getTemplateName());
        getFields().clear();
        for (PartField f : tpdata.getFields().values()) {
            try {
                getFields().put(f.getName(), (PartField) f.clone());
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

    public String getEditTitle() {
        return getTemplateName() + ", ID=" + getId();
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        rdata.getAttributes().put(PagePartData.KEY_PART, this);
        Template tpl = TemplateCache.getInstance().getTemplate("part", templateName);
        if (tpl==null)
            return;
        tpl.appendHtml(sb, rdata);
        rdata.getAttributes().remove(PagePartData.KEY_PART);
    }

    public Map<String, PartField> getFields() {
        return fields;
    }

    public PartTextField ensureTextField(String name) {
        PartField field = fields.get(name);
        if (field instanceof PartTextField)
            return (PartTextField) field;
        PartTextField textfield = new PartTextField();
        textfield.setName(name);
        textfield.setPartId(getId());
        fields.put(name, textfield);
        return textfield;
    }

    public PartHtmlField ensureHtmlField(String name) {
        PartField field = fields.get(name);
        if (field instanceof PartHtmlField)
            return (PartHtmlField) field;
        PartHtmlField htmlfield = new PartHtmlField();
        htmlfield.setName(name);
        htmlfield.setPartId(getId());
        fields.put(name, htmlfield);
        return htmlfield;
    }

    @Override
    public void setCreateValues(RequestData rdata) {
        super.setCreateValues(rdata);
        setTemplateName(rdata.getAttributes().getString("layout"));
    }

    @Override
    public void readFrontendRequestData(RequestData rdata) {
        super.readFrontendRequestData(rdata);
        for (PartField field : getFields().values()) {
            field.readFrontendRequestData(rdata);
        }
    }

    public PartField getNewField(String type) {
        return switch (type) {
            case PartTextField.FIELDTYPE -> new PartTextField();
            case PartHtmlField.FIELDTYPE -> new PartHtmlField();
            default -> null;
        };
    }

}
