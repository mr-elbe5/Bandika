/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.company;

import de.elbe5.application.AdminController;
import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BaseData;
import de.elbe5.request.*;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.CloseDialogView;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

public class CompanyController extends Controller {

    public static final String KEY = "company";

    private static CompanyController instance = null;

    public static void setInstance(CompanyController instance) {
        CompanyController.instance = instance;
    }

    public static CompanyController getInstance() {
        return instance;
    }

    public static void register(CompanyController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openEditCompany(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int companyId = rdata.getId();
        CompanyData data = CompanyBean.getInstance().getCompany(companyId);
        rdata.setSessionObject("companyData", data);
        return showEditCompany();
    }

    public IView openCreateCompany(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        CompanyData data = new CompanyData();
        data.setNew(true);
        data.setId(CompanyBean.getInstance().getNextId());
        rdata.setSessionObject("companyData", data);
        return showEditCompany();
    }

    public IView saveCompany(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        CompanyData data = (CompanyData) rdata.getSessionObject("companyData");
        assert(data!=null);
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditCompany();
        }
        CompanyBean.getInstance().saveCompany(data);
        CompanyCache.setDirty();
        rdata.setMessage(Strings.string("_companySaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogView("/ctrl/admin/openPersonAdministration?companyId=" + data.getId());
    }

    public IView deleteCompany(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int id = rdata.getId();
        if (id < BaseData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return new UrlView("/ctrl/admin/openPersonAdministration");
        }
        CompanyBean.getInstance().deleteCompany(id);
        CompanyCache.setDirty();
        rdata.setMessage(Strings.string("_companyDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new UrlView("/ctrl/admin/openPersonAdministration");
    }

    protected IView showEditCompany() {
        return new UrlView("/WEB-INF/_jsp/company/editCompany.ajax.jsp");
    }
}
