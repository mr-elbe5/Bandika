/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.log.Log;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.servlet.*;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;

import java.util.List;

public class TemplateController extends Controller {

    public static final String KEY = "template";

    private static TemplateController instance=new TemplateController();

    public static TemplateController getInstance() {
        return instance;
    }

    @Override
    public String getKey(){
        return KEY;
    }

    public IActionResult openImportTemplates(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        return showImportTemplates(rdata);
    }

    public IActionResult importTemplates(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        String code = rdata.getString("code");
        List<TemplateData> templates = TemplateParser.parseTemplates(code);
        boolean success=true;
        for (TemplateData template : templates) {
            template.setNew(TemplateBean.getInstance().getTemplate(template.getName(), template.getType()) == null);
            if (!TemplateBean.getInstance().saveTemplate(template)) {
                Log.error("could not save template " + template.getName());
                success = false;
            }
        }
        if (!success) {
            rdata.setMessage(Strings._importError.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showImportTemplates(rdata);
        }
        TemplateBean.getInstance().writeAllTemplateFiles();
        rdata.setMessage(Strings._templatesImported.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openContentAdministration");
    }

    public IActionResult openCreateTemplate(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        TemplateData data = new TemplateData();
        data.setType(rdata.getString("templateType"));
        data.setNew(true);
        rdata.setSessionObject("templateData", data);
        return showEditTemplate(rdata);
    }

    public IActionResult openEditTemplate(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        String templateName = rdata.getString("templateName");
        String templateType = rdata.getString("templateType");
        TemplateData data = TemplateBean.getInstance().getTemplate(templateName, templateType);
        if (data == null) {
            return forbidden(rdata);
        }
        rdata.setSessionObject("templateData", data);
        return showEditTemplate(rdata);
    }

    public IActionResult saveTemplate(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        TemplateData data = (TemplateData) rdata.getSessionObject("templateData");
        if (data == null)
            return noData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditTemplate(rdata);
        }
        TemplateBean.getInstance().saveTemplate(data);
        TemplateBean.getInstance().writeTemplateFile(data);
        rdata.setMessage(Strings._templateSaved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openContentAdministration");
    }

    public IActionResult deleteTemplate(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        String templateName = rdata.getString("templateName");
        String templateType = rdata.getString("templateType");
        if (!TemplateBean.getInstance().deleteTemplate(templateName, templateType))
            return forbidden(rdata);
        TemplateBean.getInstance().deleteTemplateFile(templateName, templateType);
        rdata.setMessage(Strings._templateDeleted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/admin/openContentAdministration");
    }

    private IActionResult showImportTemplates(RequestData rdata) {
        return new ForwardActionResult("/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    private IActionResult showEditTemplate(RequestData rdata) {
        return new ForwardActionResult("/WEB-INF/_jsp/template/editTemplate.ajax.jsp");
    }

}
