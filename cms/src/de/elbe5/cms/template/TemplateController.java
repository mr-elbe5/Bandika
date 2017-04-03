/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.rights.IRights;
import de.elbe5.webserver.application.Controller;
import de.elbe5.base.data.*;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.tree.TreeNodeRightsData;
import de.elbe5.webserver.tree.TreeRightsProvider;
import de.elbe5.webserver.servlet.RequestError;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class TemplateController extends Controller implements IActionController {
    private static TemplateController instance = null;

    public static TemplateController getInstance() {
        return instance;
    }

    public static void setInstance(TemplateController instance) {
        TemplateController.instance = instance;
    }

    public TemplateController() {
        addListener(TemplateCache.getInstance());
    }

    @Override
    public String getKey() {
        return "template";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, IRights.ID_GENERAL, TreeNodeRightsData.RIGHT_EDIT)) {
            if (action.equals("openCreateTemplate")) return openCreateTemplate(request, response);
            if (action.equals("showTemplateProperties")) return showTemplateProperties(request, response);
            if (action.equals("openEditTemplate")) return openEditTemplate(request, response);
            if (action.equals("downloadTemplate")) return downloadTemplate(request, response);
            if (action.equals("saveTemplate")) return saveTemplate(request, response);
            if (action.equals("openDeleteTemplate")) return openDeleteTemplate(request, response);
            if (action.equals("deleteTemplate")) return deleteTemplate(request, response);
            if (action.equals("openImportTemplates")) return openImportTemplates(request, response);
            if (action.equals("importTemplates")) return importTemplates(request, response);
        }
        return badRequest();
    }

    public boolean showEditTemplate(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/template/editTemplate.ajax.jsp");
    }

    public boolean showDeleteTemplate(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/template/deleteTemplate.ajax.jsp");
    }

    public boolean showImportTemplates(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    public boolean showTemplateProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String templateType = RequestHelper.getString(request, "templateType");
        String templateName = RequestHelper.getString(request, "templateName");
        TemplateData data = TemplateCache.getInstance().getTemplate(templateType, templateName);
        if (data==null)
            return false;
        DataProperties props=data.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String templateType = RequestHelper.getString(request, "templateType");
        String templateName = RequestHelper.getString(request, "templateName");
        TemplateData data = TemplateCache.getInstance().getTemplate(templateType, templateName);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "templateData", data);
        return showEditTemplate(request, response);
    }

    public boolean downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String templateType = RequestHelper.getString(request, "templateType");
        String templateName = RequestHelper.getString(request, "templateName");
        String code = TemplateBean.getInstance().readTemplateFile(templateType, templateName);
        return ResponseHelper.sendBinaryResponse(request, response, templateName, "text/plain", code.getBytes(), true);
    }

    public boolean openCreateTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemplateData data = new TemplateData();
        data.setNew(true);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "templateData", data);
        return showEditTemplate(request, response);
    }

    public boolean saveTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemplateData data = (TemplateData) getSessionObject(request, "templateData");
        if (!readTemplateRequestData(data, request)) return showEditTemplate(request, response);
        String code=readTemplateCodeRequestData(request);
        if (code==null) return showEditTemplate(request, response);
        TemplateBean.getInstance().writeTemplateFile(data.getType(),data.getFileName(), code);
        TemplateCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&templateType="+data.getType()+"&templateName="+data.getFileName()+"&type="+data.getType(), "_templateSaved");
    }

    public String readTemplateCodeRequestData(HttpServletRequest request) {
        BinaryFileData file = RequestHelper.getFile(request, "file");
        if (file != null && file.getBytes() != null) {
            return new String(file.getBytes());
        }
        return null;
    }

    public boolean readTemplateRequestData(TemplateData data, HttpServletRequest request) {
        data.setDescription(RequestHelper.getString(request, "description"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        return true;
    }

    public boolean openDeleteTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showDeleteTemplate(request, response);
    }

    public boolean deleteTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String templateType = RequestHelper.getString(request, "templateType");
        String templateName = RequestHelper.getString(request, "templateName");
        TemplateBean.getInstance().deleteTemplateFile(templateType, templateName);
        TemplateCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&templateType="+templateType, "_templateDeleted");
    }

    public boolean openImportTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, List<TemplateData>> templates=TemplateBean.getInstance().getAllTemplatesFromImport();
        SessionHelper.setSessionObject(request,"templates",templates);
        return showImportTemplates(request, response);
    }

    @SuppressWarnings("unchecked")
    public boolean importTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, List<TemplateData>> templates= (Map<String, List<TemplateData>>) SessionHelper.getSessionObject(request, "templates");
        if (templates==null){
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_noData", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        List<String> masterNames=RequestHelper.getStringList(request,"masterTemplate");
        for (TemplateData data : templates.get(TemplateData.TYPE_MASTER)){
            if (masterNames.contains(data.getFileName()))
                TemplateBean.getInstance().writeTemplateFile(TemplateData.TYPE_MASTER,data.getFileName(),data.getCode());
        }
        List<String> pageNames=RequestHelper.getStringList(request,"pageTemplate");
        for (TemplateData data : templates.get(TemplateData.TYPE_PAGE)){
            if (pageNames.contains(data.getFileName()))
                TemplateBean.getInstance().writeTemplateFile(TemplateData.TYPE_PAGE,data.getFileName(),data.getCode());
        }
        List<String> partNames=RequestHelper.getStringList(request,"partTemplate");
        for (TemplateData data : templates.get(TemplateData.TYPE_PART)){
            if (partNames.contains(data.getFileName()))
                TemplateBean.getInstance().writeTemplateFile(TemplateData.TYPE_PART,data.getFileName(),data.getCode());
        }
        SessionHelper.removeSessionObject(request,"templates");
        TemplateCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_templatesImported");
    }

}
