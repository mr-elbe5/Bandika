/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.group;

import de.elbe5.base.Strings;
import de.elbe5.base.BaseData;
import de.elbe5.group.html.EditGroupPage;
import de.elbe5.request.*;
import de.elbe5.response.*;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.UserCache;

import javax.servlet.http.HttpServletResponse;

public class GroupController extends Controller {

    public static final String KEY = "group";

    private static GroupController instance = null;

    public static void setInstance(GroupController instance) {
        GroupController.instance = instance;
    }

    public static GroupController getInstance() {
        return instance;
    }

    public static void register(GroupController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse openEditGroup(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int groupId = rdata.getId();
        GroupData data = GroupBean.getInstance().getGroup(groupId);
        rdata.setSessionObject("groupData", data);
        return showEditGroup(rdata);
    }

    public IResponse openCreateGroup(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        GroupData data = new GroupData();
        data.setNew(true);
        data.setId(GroupBean.getInstance().getNextId());
        rdata.setSessionObject("groupData", data);
        return showEditGroup(rdata);
    }

    public IResponse saveGroup(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        GroupData data = (GroupData) rdata.getSessionObject("groupData");
        if (data==null){
            return new StatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditGroup(rdata);
        }
        GroupBean.getInstance().saveGroup(data);
        UserCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openUserAdministration?groupId=" + data.getId(), Strings.getString("_groupSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    public IResponse deleteGroup(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int id = rdata.getId();
        if (id < BaseData.ID_MIN) {
            rdata.setMessage(Strings.getString("_notDeletable"), RequestKeys.MESSAGE_TYPE_ERROR);
            return new ForwardResponse("/ctrl/admin/openUserAdministration");
        }
        GroupBean.getInstance().deleteGroup(id);
        UserCache.setDirty();
        rdata.setMessage(Strings.getString("_groupDeleted"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new ForwardResponse("/ctrl/admin/openUserAdministration");
    }

    protected IResponse showEditGroup(RequestData rdata) {
        return new EditGroupPage().createHtml(rdata);
    }
}
