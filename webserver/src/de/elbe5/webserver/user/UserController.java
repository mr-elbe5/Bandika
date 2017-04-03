/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.user;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.data.*;
import de.elbe5.base.rights.IRights;
import de.elbe5.base.user.GroupData;
import de.elbe5.base.user.UserData;
import de.elbe5.webserver.application.Controller;
import de.elbe5.webserver.configuration.GeneralRightsData;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.base.event.Event;
import de.elbe5.base.rights.RightsCache;
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
import java.util.Locale;

public class UserController extends Controller implements IActionController {
    private static UserController instance = null;

    public static UserController getInstance() {
        return instance;
    }

    public static void setInstance(UserController instance) {
        UserController.instance = instance;
    }

    public UserController() {
        addListener(RightsCache.getInstance());
    }

    @Override
    public String getKey() {
        return "user";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL, GeneralRightsData.RIGHT_USER_ADMIN)) {
            if (action.equals("openAddGroupUser")) return openAddGroupUser(request, response);
            if (action.equals("addGroupUser")) return addGroupUser(request, response);
            if (action.equals("openRemoveGroupUsers")) return openRemoveGroupUsers(request, response);
            if (action.equals("removeGroupUsers")) return removeGroupUsers(request, response);
            if (action.equals("showUserProperties")) return showUserProperties(request, response);
            if (action.equals("openEditUser")) return openEditUser(request, response);
            if (action.equals("openCreateUser")) return openCreateUser(request, response);
            if (action.equals("saveUser")) return saveUser(request, response);
            if (action.equals("openDeleteUser")) return openDeleteUser(request, response);
            if (action.equals("deleteUser")) return deleteUser(request, response);
            if (action.equals("showGroupProperties")) return showGroupProperties(request, response);
            if (action.equals("openEditGroup")) return openEditGroup(request, response);
            if (action.equals("openCreateGroup")) return openCreateGroup(request, response);
            if (action.equals("saveGroup")) return saveGroup(request, response);
            if (action.equals("openDeleteGroup")) return openDeleteGroup(request, response);
            if (action.equals("deleteGroup")) return deleteGroup(request, response);
        }
        return badRequest();
    }

    protected boolean showEditUser(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected boolean showDeleteUser(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/deleteUser.ajax.jsp");
    }

    protected boolean showEditGroup(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/editGroup.ajax.jsp");
    }

    protected boolean showDeleteGroup(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/deleteGroup.ajax.jsp");
    }

    protected boolean showAddGroupUser(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/addGroupUser.ajax.jsp");
    }

    protected boolean showRemoveGroupUsers(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/removeGroupUsers.ajax.jsp");
    }

    public boolean showUserProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int userId = RequestHelper.getInt(request, "userId");
        UserData data = UserBean.getInstance().getUser(userId);
        for (int groupId : data.getGroupIds()){
            data.getGroups().add(UserBean.getInstance().getGroup(groupId));
        }
        Locale locale= SessionHelper.getSessionLocale(request);
        DataProperties props=data.getProperties(locale);
        StringBuilder sb=new StringBuilder();
        for (GroupData group : data.getGroups()){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.toHtml(group.getName()));
        }
        props.addKeyPropertyLines("_groups", sb.toString(),locale);
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int userId = RequestHelper.getInt(request, "userId");
        UserBean ts = UserBean.getInstance();
        UserData data = ts.getUser(userId);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "userData", data);
        return showEditUser(request, response);
    }

    public boolean openCreateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserBean ts = UserBean.getInstance();
        UserData data = new UserData();
        data.setNew(true);
        data.setId(ts.getNextId());
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "userData", data);
        return showEditUser(request, response);
    }

    public boolean saveUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserData data = (UserData) getSessionObject(request, "userData");
        if (!readUserRequestData(data, request)) return showEditUser(request, response);
        UserBean ts = UserBean.getInstance();
        ts.saveUser(data);
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        if (SessionHelper.getUserId(request) == data.getId()) SessionHelper.setSessionUserData(request, data);
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&userId="+data.getId(), "_userSaved");
    }

    public boolean readUserRequestData(UserData data, HttpServletRequest request) {
        data.setFirstName(RequestHelper.getString(request, "firstName"));
        data.setLastName(RequestHelper.getString(request, "lastName"));
        data.setEmail(RequestHelper.getString(request, "email"));
        data.setLogin(RequestHelper.getString(request, "login"));
        data.setPassword(RequestHelper.getString(request, "password"));
        data.setApproved(RequestHelper.getBoolean(request, "approved"));
        data.setGroupIds(RequestHelper.getIntegerSet(request, "groupIds"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        return true;
    }

    public boolean openDeleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "userId");
        if (id == SessionHelper.getUserId(request)) ResponseHelper.addError(request, StringUtil.getHtml("_noSelfDelete", SessionHelper.getSessionLocale(request)));
        if (id < BaseIdData.ID_MIN) ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
        return showDeleteUser(request, response);
    }

    public boolean deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "userId");
        UserBean ts = UserBean.getInstance();
        if (id < BaseIdData.ID_MIN) ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
        else {
            ts.deleteUser(id);
            sendEvent(new Event(BaseCache.EVENT_DIRTY));
        }
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_usersDeleted");
    }

    public boolean showGroupProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int groupId = RequestHelper.getInt(request, "groupId");
        GroupData data = UserBean.getInstance().getGroup(groupId);
        for (int userId : data.getUserIds()){
            data.getUsers().add(UserBean.getInstance().getUser(userId));
        }
        Locale locale=SessionHelper.getSessionLocale(request);
        DataProperties props=data.getProperties(locale);
        StringBuilder sb=new StringBuilder();
        for (UserData user : data.getUsers()){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.toHtml(user.getName()));
        }
        props.addKeyPropertyLines("_users", sb.toString(),locale);
        sb=new StringBuilder();
        IRights rights = RightsCache.getInstance().getRights(data.getId(),GeneralRightsProvider.RIGHTS_TYPE_GENERAL);
        if (rights.hasRight(GeneralRightsData.RIGHT_APPLICATION_ADMIN))
            sb.append(StringUtil.getHtml("_application"));
        if (rights.hasRight(GeneralRightsData.RIGHT_USER_ADMIN)){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.getHtml("_users"));
        }
        if (rights.hasRight(GeneralRightsData.RIGHT_CONTENT_ADMIN)){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.getHtml("_content"));
        }
        props.addKeyPropertyLines("_generalRights", sb.toString(),locale);
        sb=new StringBuilder();
        rights = RightsCache.getInstance().getRights(data.getId(), TreeRightsProvider.RIGHTS_TYPE_TREENODE);
        if (rights.hasRight(TreeNodeRightsData.RIGHT_APPROVE))
            sb.append(StringUtil.getHtml("_approve"));
        if (rights.hasRight(TreeNodeRightsData.RIGHT_EDIT)){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.getHtml("_edit"));
        }
        if (rights.hasRight(TreeNodeRightsData.RIGHT_READ)){
            if (sb.length()>0)
                sb.append("\n");
            sb.append(StringUtil.getHtml("_read"));
        }
        props.addKeyPropertyLines("_treeNodeRights", sb.toString(),locale);
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int groupId = RequestHelper.getInt(request, "groupId");
        UserBean ts = UserBean.getInstance();
        GroupData data = ts.getGroup(groupId);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "groupData", data);
        return showEditGroup(request, response);
    }

    public boolean openCreateGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserBean ts = UserBean.getInstance();
        GroupData data = new GroupData();
        data.setNew(true);
        data.setId(ts.getNextId());
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "groupData", data);
        return showEditGroup(request, response);
    }

    public boolean openAddGroupUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int groupId = RequestHelper.getInt(request, "groupId");
        UserBean ts = UserBean.getInstance();
        GroupData data = ts.getGroup(groupId);
        SessionHelper.setSessionObject(request, "groupData", data);
        return showAddGroupUser(request, response);
    }

    public boolean addGroupUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GroupData data = (GroupData) getSessionObject(request, "groupData");
        readGroupRequestData(data, request);
        int userId = RequestHelper.getInt(request, "userAddId");
        if (userId != 0) {
            data.getUserIds().add(userId);
            RequestHelper.setMessageKey(request, "_userAdded");
        }
        UserBean.getInstance().saveGroupUsers(data);
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&groupId="+data.getId(), "_userAdded");
    }

    public boolean openRemoveGroupUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int groupId = RequestHelper.getInt(request, "groupId");
        UserBean ts = UserBean.getInstance();
        GroupData data = ts.getGroup(groupId);
        SessionHelper.setSessionObject(request, "groupData", data);
        return showRemoveGroupUsers(request, response);
    }

    public boolean removeGroupUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GroupData data = (GroupData) getSessionObject(request, "groupData");
        readGroupRequestData(data, request);
        List<Integer> ids = RequestHelper.getIntegerList(request, "userRemoveId");
        for (int userId : ids)
            data.getUserIds().remove(userId);
        UserBean.getInstance().saveGroupUsers(data);
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&groupId="+data.getId(), "_usersRemoved");
    }

    public boolean saveGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GroupData data = (GroupData) getSessionObject(request, "groupData");
        if (!readGroupRequestData(data, request))
            return showEditGroup(request, response);
        UserBean ts = UserBean.getInstance();
        ts.saveGroup(data);
        SessionHelper.setSessionUserData(request, ts.getUser(SessionHelper.getSessionUserData(request).getId()));
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&groupId="+data.getId(), "_groupSaved");
    }

    public boolean readGroupRequestData(GroupData data, HttpServletRequest request) {
        data.setName(RequestHelper.getString(request, "name"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        return true;
    }

    public boolean openDeleteGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "groupId");
        if (id < BaseIdData.ID_MIN) ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
        return showDeleteGroup(request, response);
    }

    public boolean deleteGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "groupId");
        UserBean ts = UserBean.getInstance();
        if (id < BaseIdData.ID_MIN) ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
        else {
            ts.deleteGroup(id);
            RightsCache.getInstance().setDirty();
        }
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_groupDeleted");
    }
}
