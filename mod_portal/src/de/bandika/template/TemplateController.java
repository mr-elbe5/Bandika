/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika.data.IChangeListener;
import de.bandika.data.IRights;
import de.bandika.data.StringCache;
import de.bandika.data.FileData;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;

public class TemplateController extends Controller {

    public static final int LINKID_TEMPLATES = 102;

    private static TemplateController instance = null;

    public static void setInstance(TemplateController instance) {
        TemplateController.instance = instance;
    }

    public static TemplateController getInstance() {
        if (instance == null) {
            instance = new TemplateController();
        }
        return instance;
    }

    public String getKey(){
        return "template";
    }

    public void initialize() {
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata)
            throws Exception {
        if (!sdata.isLoggedIn())
            return UserController.getInstance().openLogin();
        if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, IRights.ID_GENERAL, PageRightsData.RIGHT_EDIT)) {
            if (action.equals("openEditMasterTemplates")) return openEditMasterTemplates(sdata);
            if (action.equals("openCreateMasterTemplate")) return openCreateMasterTemplate(sdata);
            if (action.equals("openEditMasterTemplate")) return openEditMasterTemplate(rdata, sdata);
            if (action.equals("downloadMasterTemplate")) return downloadMasterTemplate(rdata);
            if (action.equals("saveMasterTemplate")) return saveMasterTemplate(rdata, sdata);
            if (action.equals("openDeleteMasterTemplates")) return openDeleteMasterTemplates(rdata, sdata);
            if (action.equals("deleteMasterTemplates")) return deleteMasterTemplates(rdata, sdata);
            if (action.equals("openEditLayoutTemplates")) return openEditLayoutTemplates(sdata);
            if (action.equals("openCreateLayoutTemplate")) return openCreateLayoutTemplate(sdata);
            if (action.equals("openEditLayoutTemplate")) return openEditLayoutTemplate(rdata, sdata);
            if (action.equals("downloadLayoutTemplate")) return downloadLayoutTemplate(rdata);
            if (action.equals("saveLayoutTemplate")) return saveLayoutTemplate(rdata, sdata);
            if (action.equals("openDeleteLayoutTemplates")) return openDeleteLayoutTemplates(rdata, sdata);
            if (action.equals("deleteLayoutTemplates")) return deleteLayoutTemplates(rdata, sdata);
            if (action.equals("openEditPartTemplates")) return openEditPartTemplates(sdata);
            if (action.equals("openCreatePartTemplate")) return openCreatePartTemplate(sdata);
            if (action.equals("openEditPartTemplate")) return openEditPartTemplate(rdata, sdata);
            if (action.equals("downloadPartTemplate")) return downloadPartTemplate(rdata);
            if (action.equals("savePartTemplate")) return savePartTemplate(rdata, sdata);
            if (action.equals("openDeletePartTemplates")) return openDeletePartTemplates(rdata,sdata);
            if (action.equals("deletePartTemplates")) return deletePartTemplates(rdata,sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected Response showEditAllMasters(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editAllMasterTemplates.jsp", StringCache.getString("portal_templates", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditMaster(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editMasterTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteMaster(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/deleteMasterTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openEditMasterTemplates(SessionData sdata) throws Exception {
        return showEditAllMasters(sdata);
    }

    public Response openEditMasterTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditMasterTemplates(sdata);
        }
        if (names.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditMasterTemplates(sdata);
        }
        String name = names.get(0);
        TemplateBean ts = TemplateBean.getInstance();
        MasterTemplateData data = ts.getMasterTemplate(name);
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditMaster(sdata);
    }

    public Response downloadMasterTemplate(RequestData rdata) {
        String name = rdata.getString("name");
        MasterTemplateData data = new MasterTemplateData();
        data.setName(name);
        TemplateBean.getInstance().readMasterTemplateFile(data);
        return new BinaryResponse(data.getName() + ".jsp", "text/plain", data.getCode().getBytes(), true);
    }

    public Response openCreateMasterTemplate(SessionData sdata) throws Exception {
        MasterTemplateData data = new MasterTemplateData();
        data.setNew();
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditMaster(sdata);
    }

    public Response saveMasterTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        MasterTemplateData data = (MasterTemplateData) sdata.get("templateData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readMasterTemplateRequestData(data, rdata, sdata))
            return showEditMaster(sdata);
        TemplateBean ts = TemplateBean.getInstance();
        ts.saveMasterTemplate(data);
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templateSaved", sdata.getLocale());
        return openEditMasterTemplates(sdata);
    }

    public boolean readMasterTemplateRequestData(MasterTemplateData data, RequestData rdata, SessionData sdata) {
        FileData file = rdata.getFile("file");
        if (file != null && file.getBytes() != null) {
            data.setCode(new String(file.getBytes()));
        }
        if (data.isNew()) {
            data.setName(rdata.getString("name"));
        }
        data.setDescription(rdata.getString("description"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response openDeleteMasterTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllMasters(sdata);
        }
        return showDeleteMaster(sdata);
    }

    public Response deleteMasterTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllMasters(sdata);
        }
        for (String name : names) {
            TemplateBean.getInstance().deleteMasterTemplate(name);
            itemChanged(MasterTemplateData.class.getName(), IChangeListener.ACTION_DELETED, name, 0);
        }
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templatesDeleted", sdata.getLocale());
        return showEditAllMasters(sdata);
    }

    protected Response showEditAllLayouts(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editAllLayoutTemplates.jsp", StringCache.getString("portal_templates", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditLayout(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editLayoutTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteLayout(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/deleteLayoutTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openEditLayoutTemplates(SessionData sdata) throws Exception {
        return showEditAllLayouts(sdata);
    }

    public Response openEditLayoutTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditLayoutTemplates(sdata);
        }
        if (names.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditLayoutTemplates(sdata);
        }
        String name = names.get(0);
        TemplateBean ts = TemplateBean.getInstance();
        LayoutTemplateData data = ts.getLayoutTemplate(name);
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditLayout(sdata);
    }

    public Response downloadLayoutTemplate(RequestData rdata) {
        String name = rdata.getString("name");
        LayoutTemplateData data = new LayoutTemplateData();
        data.setName(name);
        TemplateBean.getInstance().readLayoutTemplateFile(data);
        return new BinaryResponse(data.getName() + ".jsp", "text/plain", data.getCode().getBytes(), true);
    }

    public Response openCreateLayoutTemplate(SessionData sdata) throws Exception {
        LayoutTemplateData data = new LayoutTemplateData();
        data.setNew();
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditLayout(sdata);
    }

    public Response saveLayoutTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        LayoutTemplateData data = (LayoutTemplateData) sdata.get("templateData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readLayoutTemplateRequestData(data, rdata, sdata))
            return showEditLayout(sdata);
        TemplateBean ts = TemplateBean.getInstance();
        ts.saveLayoutTemplate(data);
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templateSaved", sdata.getLocale());
        return openEditLayoutTemplates(sdata);
    }

    public boolean readLayoutTemplateRequestData(LayoutTemplateData data, RequestData rdata, SessionData sdata) {
        FileData file = rdata.getFile("file");
        if (file != null && file.getBytes() != null) {
            data.setCode(new String(file.getBytes()));
        }
        if (data.isNew()) {
            data.setName(rdata.getString("name"));
            data.setClassName(rdata.getString("className"));
        }
        data.setDescription(rdata.getString("description"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response openDeleteLayoutTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllLayouts(sdata);
        }
        return showDeleteLayout(sdata);
    }

    public Response deleteLayoutTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllLayouts(sdata);
        }
        for (String name : names) {
            TemplateBean.getInstance().deleteLayoutTemplate(name);
            itemChanged(LayoutTemplateData.class.getName(), IChangeListener.ACTION_DELETED, name, 0);
        }
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templatesDeleted", sdata.getLocale());
        return showEditAllLayouts(sdata);
    }

    protected Response showEditAllParts(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editAllPartTemplates.jsp", StringCache.getString("portal_templates", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditPart(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/editPartTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeletePart(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/template/deletePartTemplate.jsp", StringCache.getString("portal_template", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openEditPartTemplates(SessionData sdata) throws Exception {
        return showEditAllParts(sdata);
    }

    public Response openEditPartTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditPartTemplates(sdata);
        }
        if (names.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditPartTemplates(sdata);
        }
        String name = names.get(0);
        TemplateBean ts = TemplateBean.getInstance();
        PartTemplateData data = ts.getPartTemplate(name);
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditPart(sdata);
    }

    public Response downloadPartTemplate(RequestData rdata) {
        String name = rdata.getString("name");
        PartTemplateData data = new PartTemplateData();
        data.setName(name);
        TemplateBean.getInstance().readPartTemplateFile(data);
        return new BinaryResponse(data.getName() + ".jsp", "text/plain", data.getCode().getBytes(), true);
    }

    public Response openCreatePartTemplate(SessionData sdata) throws Exception {
        PartTemplateData data = new PartTemplateData();
        data.setNew();
        data.prepareEditing();
        sdata.put("templateData", data);
        return showEditPart(sdata);
    }

    public Response savePartTemplate(RequestData rdata, SessionData sdata)
            throws Exception {
        PartTemplateData data = (PartTemplateData) sdata.get("templateData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readPartTemplateRequestData(data, rdata, sdata))
            return showEditPart(sdata);
        TemplateBean ts = TemplateBean.getInstance();
        ts.savePartTemplate(data);
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templateSaved", sdata.getLocale());
        return openEditPartTemplates(sdata);
    }

    public boolean readPartTemplateRequestData(PartTemplateData data, RequestData rdata, SessionData sdata) {
        FileData file = rdata.getFile("file");
        if (file != null && file.getBytes() != null) {
            data.setCode(new String(file.getBytes()));
        }
        if (data.isNew()) {
            data.setName(rdata.getString("name"));
            data.setClassName(rdata.getString("className"));
        }
        data.setDescription(rdata.getString("description"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response openDeletePartTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllParts(sdata);
        }
        return showDeletePart(sdata);
    }

    public Response deletePartTemplates(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showEditAllParts(sdata);
        }
        for (String name : names) {
            TemplateBean.getInstance().deletePartTemplate(name);
            itemChanged(PartTemplateData.class.getName(), IChangeListener.ACTION_DELETED, name, 0);
        }
        TemplateCache.getInstance().setDirty();
        rdata.setMessageKey("portal_templatesDeleted", sdata.getLocale());
        return showEditAllParts(sdata);
    }


}
