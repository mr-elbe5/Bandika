/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.log.Log;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.servlet.*;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class TemplateActions extends ActionSet {

    public static final String openImportTemplates="openImportTemplates";
    public static final String importTemplates="importTemplates";
    public static final String openCreateTemplate="openCreateTemplate";
    public static final String openEditTemplate="openEditTemplate";
    public static final String saveTemplate="saveTemplate";
    public static final String deleteTemplate="deleteTemplate";

    public static final String KEY = "template";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TemplateActions());
    }

    private TemplateActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case openImportTemplates: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                return showImportTemplates(request, response);
            }
            case importTemplates: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                String code = RequestReader.getString(request, "code");
                if (!importTemplates(code)) {
                    ErrorMessage.setMessageByKey(request, Strings._importError);
                    return showImportTemplates(request, response);
                }
                TemplateBean.getInstance().writeAllTemplateFiles();
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openContentAdministration,Strings._templatesImported);
            }
            case openCreateTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                TemplateData data = new TemplateData();
                data.setType(RequestReader.getString(request, "templateType"));
                data.setNew(true);
                SessionWriter.setSessionObject(request, "templateData", data);
                return showEditTemplate(request, response);
            }
            case openEditTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                String templateName = RequestReader.getString(request, "templateName");
                String templateType = RequestReader.getString(request, "templateType");
                TemplateData data = TemplateBean.getInstance().getTemplate(templateName, templateType);
                if (data == null) {
                    return forbidden(request,response);
                }
                SessionWriter.setSessionObject(request, "templateData", data);
                return showEditTemplate(request, response);
            }
            case saveTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                TemplateData data = (TemplateData) RequestReader.getSessionObject(request, "templateData");
                if (data==null)
                    return noData(request,response);
                if (!data.readRequestData(request)) {
                    ErrorMessage.setMessageByKey(request, Strings._notComplete);
                    return showEditTemplate(request, response);
                }
                TemplateBean.getInstance().saveTemplate(data);
                TemplateBean.getInstance().writeTemplateFile(data);
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openContentAdministration,Strings._templateSaved);
            }
            case deleteTemplate: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                String templateName = RequestReader.getString(request, "templateName");
                String templateType = RequestReader.getString(request, "templateType");
                if (!TemplateBean.getInstance().deleteTemplate(templateName, templateType))
                    return forbidden(request,response);
                TemplateBean.getInstance().deleteTemplateFile(templateName, templateType);
                SuccessMessage.setMessageByKey(request, Strings._templateDeleted);
                return sendForwardResponse(request,response,"/admin.srv?act="+ AdminActions.openContentAdministration);
            }
            default: {
                return forbidden(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean importTemplates(String src) {
        List<TemplateData> templates =  TemplateParser.parseTemplates(src);
        for (TemplateData template : templates){
            template.setNew(TemplateBean.getInstance().getTemplate(template.getName(), template.getType())== null);
            if (!TemplateBean.getInstance().saveTemplate(template))
                Log.error("could not save template "+template.getName());
        }
        return true;
    }

    public boolean showImportTemplates(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    public boolean showEditTemplate(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/editTemplate.ajax.jsp");
    }

}
