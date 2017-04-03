/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.data.ILoginData;
import de.bandika.data.IRights;
import de.bandika.data.StatefulBaseData;
import de.bandika.rights.RightsCache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class UserData extends StatefulBaseData implements ILoginData {

    public static final String DATAKEY = "data|user";

    public static final int ROOT_ID = 1;

    protected static final byte[] passwordBase = "1234567890qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM".getBytes();

    protected int id = 0;
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
    protected Map<String, String> profile = new HashMap<>();

    protected Set<Integer> groupIds = new HashSet<>();
    protected Map<String, IRights> rights = new HashMap<>();
    protected int rightsVersion=-1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        if (firstName.length() == 0)
            return lastName;
        return firstName + " " + lastName;
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

    public void generatePassword() {
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++)
            bytes[i] = passwordBase[Math.min(
                    (int) ((Math.random() * passwordBase.length)),
                    passwordBase.length - 1)];
        setPassword(new String(bytes));
    }

    public boolean isCompletePassword() {
        return (!isNew() && password.length() == 0)
                || isComplete(password);
    }

    public boolean isRoot() {
        return id == ROOT_ID;
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

    public Map<String, String> getProfile() {
        return profile;
    }

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public Map<String, IRights> getRights() {
        return rights;
    }

    @Override
    public void checkRights(){
        int ver=RightsCache.getInstance().getVersion();
        if (ver>rightsVersion){
            rights=RightsCache.getInstance().getUserRights(this);
            rightsVersion=ver;
        }
    }

    @Override
    public boolean hasRight(String type){
        return isRoot() || (rights.get(type)!=null && rights.get(type).hasRight());
    }

    @Override
    public boolean hasRight(String type, int right){
        return isRoot() || (rights.get(type)!=null && rights.get(type).hasRight(right));
    }

    @Override
    public boolean hasRight(String type, int id, int right) {
        return isRoot() || (rights.get(type)!=null && (rights.get(type).hasRight(IRights.ID_GENERAL,right) || rights.get(type).hasRight(id,right)));
    }

    @Override
    public boolean isComplete() {
        return isComplete(login) && isCompletePassword()
                && isComplete(email)
                && isComplete(lastName);
    }

    public boolean isCompleteProfile() {
        return isComplete(login)
                && isComplete(email)
                && isComplete(lastName);
    }


}
