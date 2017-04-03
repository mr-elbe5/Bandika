/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.data;

import java.util.HashSet;
import java.util.HashMap;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class UserData extends VersionedData {

  public static final int ROOT_ID=1;

  protected static final byte[] passwordBase = "1234567890qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM".getBytes();

  protected String firstName = "";
  protected String lastName = "";
  protected String email = "";
  protected String login = "";
  protected String password = "";
  protected boolean isAdmin = false;
  protected String approvalCode = "";
  protected boolean approved = false;
  protected int failedLoginCount = 0;
  protected boolean locked = false;
  protected boolean deleted = false;
  protected HashMap<String,String> profile=new HashMap<String,String>();

  protected HashSet<Integer> groupIds = new HashSet<Integer>();

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
    if (firstName.length()==0)
      return lastName;
    return firstName+" "+lastName;
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
      bytes[i] = passwordBase[Math.min((int) ((Math.random() * passwordBase.length)), passwordBase.length - 1)];
    setPassword(new String(bytes));
  }

  public boolean isCompletePassword() {
    return (!isBeingCreated() && password.length() == 0) || DataHelper.isComplete(password);
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  public boolean isRoot(){
    return id==ROOT_ID;
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

  public HashMap<String, String> getProfile() {
    return profile;
  }

  public HashSet<Integer> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(HashSet<Integer> groupIds) {
    this.groupIds = groupIds;
  }

  @Override
  public boolean isComplete() {
    return DataHelper.isComplete(login) && isCompletePassword() && DataHelper.isComplete(email) && DataHelper.isComplete(lastName);
  }

}
