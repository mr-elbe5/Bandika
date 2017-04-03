/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.user;

import de.elbe5.base.data.*;
import de.elbe5.base.rights.IRights;
import de.elbe5.base.rights.RightsCache;

import java.util.*;

public class UserData extends BaseIdData {
    public static final int ROOT_ID = 1;
    protected String firstName = "";
    protected String lastName = "";
    protected String email = "";
    protected String login = "";
    protected String password = "";
    protected String approvalCode = "";
    protected boolean approved = false;
    protected int failedLoginCount = 0;
    protected boolean locked = false;
    protected boolean deleted = false;
    protected Set<Integer> groupIds = new HashSet<>();
    protected Map<String, IRights> rights = new HashMap<>();
    protected int rightsVersion = -1;

    protected List<GroupData> groups=new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        if (firstName.length() == 0) return lastName;
        return firstName + ' ' + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCompletePassword() {
        return (!isNew() && password.length() == 0) || isComplete(password);
    }

    public boolean isRoot() {
        return getId() == ROOT_ID;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public void setFailedLoginCount(int failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public List<GroupData> getGroups() {
        return groups;
    }

    public Map<String, IRights> getRights() {
        return rights;
    }

    public void checkRights() {
        int ver = RightsCache.getInstance().getVersion();
        if (ver > rightsVersion) {
            rights = RightsCache.getInstance().getUserRights(getGroupIds());
            rightsVersion = ver;
        }
    }

    public boolean hasAnyRight(String type) {
        return isRoot() || (rights.get(type) != null && rights.get(type).hasAnyRight());
    }

    public boolean hasRight(String type, int right) {
        return isRoot() || (rights.get(type) != null && rights.get(type).hasRight(right));
    }

    public boolean hasRightForId(String type, int id, int right) {
        return isRoot() || (rights.get(type) != null && (rights.get(type).hasRightForId(IRights.ID_GENERAL, right) || rights.get(type).hasRightForId(id, right)));
    }

    @Override
    protected void fillProperties(DataProperties properties, Locale locale){
        properties.setKeyHeader("_user", locale);
        properties.addKeyProperty("_id", getId(),locale);
        properties.addKeyProperty("_firstName", getFirstName(),locale);
        properties.addKeyProperty("_lastName", getLastName(),locale);
        properties.addKeyProperty("_login", getLogin(),locale);
        properties.addKeyProperty("_email", getEmail(),locale);
        properties.addKeyProperty("_approved", isApproved() ? "X" : "-",locale);
        properties.addKeyProperty("_failedLogins", getFailedLoginCount(),locale);
        properties.addKeyProperty("_locked", isLocked() ? "X" : "-",locale);
    }

    @Override
    public boolean isComplete() {
        return isComplete(login) && isCompletePassword() && isComplete(email) && isComplete(lastName);
    }

    public boolean isCompleteProfile() {
        return isComplete(login) && isComplete(email) && isComplete(lastName);
    }
}
