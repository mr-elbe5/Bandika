/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.tree;

import de.elbe5.webserver.application.Controller;
import de.elbe5.webserver.servlet.HttpException;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.base.user.GroupData;
import de.elbe5.webserver.user.UserBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class BaseTreeController extends Controller {

    protected void checkReadRights(HttpServletRequest request, int nodeId) {
        if (!SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, nodeId, TreeNodeRightsData.RIGHTS_READER))
            throw new HttpException(HttpServletResponse.SC_FORBIDDEN);
    }

    protected void checkEditRights(HttpServletRequest request, int nodeId) {
        if (!SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, nodeId, TreeNodeRightsData.RIGHTS_EDITOR))
            throw new HttpException(HttpServletResponse.SC_FORBIDDEN);
    }

    protected void checkApproveRights(HttpServletRequest request, int nodeId) {
        if (!SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, nodeId, TreeNodeRightsData.RIGHTS_APPROVER))
            throw new HttpException(HttpServletResponse.SC_FORBIDDEN);
    }

    public void setCreateValues(TreeNode data, TreeNode parent) {
        data.setNew(true);
        data.setId(TreeBean.getInstance().getNextId());
        data.setParentId(parent.getId());
        data.setParent(parent);
        data.setAnonymous(parent.isAnonymous());
        data.setInheritsRights(true);
        data.inheritPathFromParent();
        data.inheritRightsFromParent();
        data.inheritParentIdsFromParent();
        data.setVisible(parent.isVisible());
    }

    protected void readTreeNodeRequestData(HttpServletRequest request, TreeNode data) {
        data.setName(RequestHelper.getString(request, "name"));
        data.setDisplayName(RequestHelper.getString(request, "displayName"));
        data.setDescription(RequestHelper.getString(request, "description"));
        data.setVisible(RequestHelper.getBoolean(request, "visible"));
        data.setAnonymous(RequestHelper.getBoolean(request, "anonymous"));
        data.setInheritsRights(RequestHelper.getBoolean(request, "inheritsRights"));
    }

    protected void readTreeNodeRightsData(HttpServletRequest request, TreeNode data) {
        List<GroupData> groups = UserBean.getInstance().getAllGroups();
        data.getRights().clear();
        if (!data.inheritsRights()) {
            for (GroupData group : groups) {
                data.getRights().put(group.getId(), RequestHelper.getInt(request, "groupright_" + group.getId()));
            }
        }
    }

    protected void readResourceNodeRequestData(HttpServletRequest request, ResourceNode data) {
        readTreeNodeRequestData(request, data);
        data.setKeywords(RequestHelper.getString(request, "keywords"));
    }

}

