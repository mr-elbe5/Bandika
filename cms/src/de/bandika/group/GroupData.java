/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.group;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.rights.Right;
import de.bandika.rights.CmsRightBean;
import de.bandika.rights.RightsCache;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.RequestReader;
import de.bandika.user.UserData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends BaseIdData {

    public static final int ID_ALL = 0;
    public static final int ID_GLOBAL_ADINISTRATORS = 1;
    public static final int ID_GLOBAL_APPROVERS = 2;
    public static final int ID_GLOBAL_EDITORS = 3;
    public static final int ID_GLOBAL_READERS = 4;

    public static final int ID_MAX_FINAL = 4;

    protected String name = null;
    protected String notes = "";
    protected GroupRightsData rights = new GroupRightsData();
    protected Collection<Integer> userIds = new HashSet<>();
    protected List<UserData> users = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void checkRights() {
        int ver = RightsCache.getInstance().getVersion();
        if (ver > rights.getVersion()) {
            rights = CmsRightBean.getInstance().getGroupRights(getId());
            rights.setVersion(ver);
        }
    }

    public GroupRightsData getRights() {
        return rights;
    }

    public Collection<Integer> getUserIds() {
        return userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, Map<Integer, Integer> groupRights) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "group");
        XmlUtil.addIntAttribute(xmlDoc, node, "id", getId());
        XmlUtil.addAttribute(xmlDoc, node, "name", StringUtil.toXml(getName()));
        XmlUtil.addAttribute(xmlDoc, node, "notes", StringUtil.toXml(getNotes()));
        Element rightsNode = XmlUtil.addNode(xmlDoc, node, "singleContentRights");
        for (Integer treeId : groupRights.keySet()) {
            Element rightNode = XmlUtil.addNode(xmlDoc, rightsNode, "right");
            XmlUtil.addIntAttribute(xmlDoc, rightNode, "id", treeId);
            XmlUtil.addIntAttribute(xmlDoc, rightNode, "right", groupRights.get(treeId));
        }
    }

    public void readGroupRequestData(HttpServletRequest request) {
        setName(RequestReader.getString(request, "name"));
        setNotes(RequestReader.getString(request, "notes"));
        getRights().getSystemRights().clear();
        for (SystemZone zone : SystemZone.values()) {
            String key = RequestReader.getString(request, "zoneright_" + zone.name());
            if (!key.isEmpty())
                getRights().addSystemRight(zone, Right.valueOf(key));
        }
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }
}
