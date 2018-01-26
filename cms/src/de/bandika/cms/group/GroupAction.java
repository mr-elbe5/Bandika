/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.group;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.servlet.CmsAction;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.RightsCache;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class GroupAction extends CmsAction {

    public static final String openAddGroupUser="openAddGroupUser";
    public static final String addGroupUser="addGroupUser";
    public static final String openRemoveGroupUsers="openRemoveGroupUsers";
    public static final String removeGroupUsers="removeGroupUsers";
    public static final String showGroupDetails="showGroupDetails";
    public static final String openEditGroup="openEditGroup";
    public static final String openCreateGroup="openCreateGroup";
    public static final String saveGroup="saveGroup";
    public static final String openDeleteGroup="openDeleteGroup";
    public static final String deleteGroup="deleteGroup";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openAddGroupUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                int groupId = RequestReader.getInt(request, "groupId");
                GroupData data = GroupBean.getInstance().getGroup(groupId);
                SessionWriter.setSessionObject(request, "groupData", data);
                return showAddGroupUser(request, response);
            }
            case addGroupUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                GroupData data = (GroupData) getSessionObject(request, "groupData");
                data.readGroupRequestData(request);
                if (!isDataComplete(data, request)) {
                    return showAddGroupUser(request, response);
                }
                int userId = RequestReader.getInt(request, "userAddId");
                if (userId != 0) {
                    data.getUserIds().add(userId);
                    RequestWriter.setMessageKey(request, "_userAdded");
                }
                GroupBean.getInstance().saveGroupUsers(data);
                RightsCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&groupId=" + data.getId(), "_userAdded");
            }
            case openRemoveGroupUsers: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                int groupId = RequestReader.getInt(request, "groupId");
                GroupData data = GroupBean.getInstance().getGroup(groupId);
                SessionWriter.setSessionObject(request, "groupData", data);
                return showRemoveGroupUsers(request, response);
            }
            case removeGroupUsers: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                GroupData data = (GroupData) getSessionObject(request, "groupData");
                data.readGroupRequestData(request);
                List<Integer> ids = RequestReader.getIntegerList(request, "userRemoveId");
                for (int userId : ids) {
                    data.getUserIds().remove(userId);
                }
                GroupBean.getInstance().saveGroupUsers(data);
                RightsCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&groupId=" + data.getId(), "_usersRemoved");
            }
            case showGroupDetails: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                return showGroupDetails(request, response);
            }
            case openEditGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                int groupId = RequestReader.getInt(request, "groupId");
                GroupData data = GroupBean.getInstance().getGroup(groupId);
                data.checkRights();
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "groupData", data);
                return showEditGroup(request, response);
            }
            case openCreateGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                GroupData data = new GroupData();
                data.setNew(true);
                data.setId(GroupBean.getInstance().getNextId());
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "groupData", data);
                return showEditGroup(request, response);
            }
            case saveGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                GroupData data = (GroupData) getSessionObject(request, "groupData");
                data.readGroupRequestData(request);
                if (!isDataComplete(data, request)) {
                    return showEditGroup(request, response);
                }
                GroupBean.getInstance().saveGroup(data);
                RightsCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&groupId=" + data.getId(), "_groupSaved");
            }
            case openDeleteGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                int id = RequestReader.getInt(request, "groupId");
                if (id < BaseIdData.ID_MIN) {
                    addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                }
                return showDeleteGroup(request, response);
            }
            case deleteGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return false;
                int id = RequestReader.getInt(request, "groupId");
                if (id < BaseIdData.ID_MIN) {
                    addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                } else {
                    GroupBean.getInstance().deleteGroup(id);
                    RightsCache.getInstance().setDirty();
                }
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_groupDeleted");
            }
            default: {
                return forbidden();
            }
        }
    }

    public static final String KEY = "group";

    public static void initialize() {
        ActionDispatcher.addAction(KEY, new GroupAction());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showEditGroup(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/group/editGroup.ajax.jsp");
    }

    protected boolean showDeleteGroup(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/group/deleteGroup.ajax.jsp");
    }

    protected boolean showAddGroupUser(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/group/addGroupUser.ajax.jsp");
    }

    protected boolean showRemoveGroupUsers(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/group/removeGroupUsers.ajax.jsp");
    }

    protected boolean showGroupDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/group/groupDetails.ajax.jsp");
    }

}
