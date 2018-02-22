/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.data.BinaryFileData;
import de.bandika.cms.application.AdminActions;
import de.bandika.cms.servlet.CmsActions;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

public class TemplateActions extends CmsActions {

    public static final String openImportTemplates="openImportTemplates";
    public static final String importTemplates="importTemplates";
    public static final String openCreateTemplate="openCreateTemplate";
    public static final String showTemplateDetails="showTemplateDetails";
    public static final String openEditTemplate="openEditTemplate";
    public static final String saveTemplate="saveTemplate";
    public static final String openDeleteTemplate="openDeleteTemplate";
    public static final String deleteTemplate="deleteTemplate";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openImportTemplates: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                return showImportTemplates(request, response);
            }
            case importTemplates: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String html = "";
                BinaryFileData file = RequestReader.getFile(request, "file");
                if (file != null && file.getBytes() != null) {
                    html = new String(file.getBytes());
                }
                if (!importTemplates(html)) {
                    addError(request, "could not import templates");
                    return showImportTemplates(request, response);
                }
                TemplateCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_templatesImported");
            }
            case openCreateTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TemplateData data = new TemplateData();
                data.setType(RequestReader.getString(request, "templateType"));
                data.setNew(true);
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "templateData", data);
                return showEditTemplate(request, response);
            }
            case showTemplateDetails: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                return showTemplateDetails(request, response);
            }
            case openEditTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String templateType = RequestReader.getString(request, "templateType");
                String templateName = RequestReader.getString(request, "templateName");
                TemplateData data = TemplateCache.getInstance().getTemplate(templateType, templateName);
                if (data == null) {
                    return false;
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "templateData", data);
                return showEditTemplate(request, response);
            }
            case saveTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TemplateData data = (TemplateData) getSessionObject(request, "templateData");
                if (data.isNew())
                    data.setName(RequestReader.getString(request, "name"));
                data.setDisplayName(RequestReader.getString(request, "displayName"));
                data.setDescription(RequestReader.getString(request, "description"));
                data.setCode(RequestReader.getString(request, "code"));
                data.setSectionTypes(RequestReader.getString(request, "sectionTypes"));
                if (!isDataComplete(data, request)) {
                    return showEditTemplate(request, response);
                }
                if (!TemplateParser.parseTemplate(data)) {
                    return showEditTemplate(request, response);
                }
                TemplateBean.getInstance().saveTemplate(data, true);
                TemplateCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration+"&templateType=" + data.getType() + "&templateName=" + data.getName(), "_templateSaved");
            }
            case openDeleteTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                return showDeleteTemplate(request, response);
            }
            case deleteTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String templateType = RequestReader.getString(request, "templateType");
                String templateName = RequestReader.getString(request, "templateName");
                if (!TemplateBean.getInstance().deleteTemplate(templateName, templateType))
                    return false;
                TemplateCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_templateDeleted");
            }
            default: {
                return forbidden();
            }
        }
    }

    public static final String KEY = "template";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TemplateActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean importTemplates(String src) {
        List<TemplateData> templates =  TemplateParser.parseTemplates(src);
        for (TemplateData template : templates){
            if (TemplateCache.getInstance().getTemplate(template.getType(), template.getName()) == null)
                template.setNew(true);
            TemplateBean.getInstance().saveTemplate(template, false);
        }
        return true;
    }

    public boolean showImportTemplates(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    public boolean showEditTemplate(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/editTemplate.ajax.jsp");
    }

    public boolean showDeleteTemplate(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/deleteTemplate.ajax.jsp");
    }

    protected boolean showTemplateDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/templateDetails.ajax.jsp");
    }

}
