/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.*;
import de.elbe5.base.log.Log;
import de.elbe5.group.GroupData;
import de.elbe5.rights.*;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;

import java.util.*;

import de.elbe5.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

public class UserData extends BaseIdData {

    public static final int ID_SYSTEM = 1;

    protected String firstName = "";
    protected String middleName = "";
    protected String lastName = "";
    protected String street = "";
    protected String zipCode = "";
    protected String city = "";
    protected String country = "";
    protected Locale locale = Locale.ENGLISH;
    protected String email = "";
    protected String phone = "";
    protected String mobile = "";
    protected String notes = "";
    protected String login = "";
    protected String password = "";
    protected String approvalCode = "";
    protected boolean approved = false;
    protected int failedLoginCount = 0;
    protected boolean locked = false;
    protected boolean deleted = false;
    protected Set<Integer> groupIds = new HashSet<>();
    protected UserRightsData rights= new UserRightsData();

    protected List<GroupData> groups = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        if (firstName.length() == 0) {
            return lastName;
        }
        return firstName + ' ' + lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(String localeName) {
        try {
            locale = new Locale(localeName);
        } catch (Exception e) {
            Log.error("locale not found: " + localeName);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public UserRightsData getRights() {
        return rights;
    }

    public void checkRights() {
        int ver = RightsCache.getInstance().getVersion();
        if (ver > rights.getVersion()) {
            rights = RightBean.getInstance().getUserRights(getGroupIds());
            rights.setVersion(ver);
        }
    }

    public void readUserRequestData(HttpServletRequest request) {
        readUserProfileRequestData(request);
        setLogin(RequestReader.getString(request, "login"));
        setPassword(RequestReader.getString(request, "password"));
        setApproved(RequestReader.getBoolean(request, "approved"));
        setGroupIds(RequestReader.getIntegerSet(request, "groupIds"));
    }

    public void readUserProfileRequestData(HttpServletRequest request) {
        setFirstName(RequestReader.getString(request, "firstName"));
        setMiddleName(RequestReader.getString(request, "middleName"));
        setLastName(RequestReader.getString(request, "lastName"));
        setStreet(RequestReader.getString(request, "street"));
        setZipCode(RequestReader.getString(request, "zipCode"));
        setCity(RequestReader.getString(request, "city"));
        setCountry(RequestReader.getString(request, "country"));
        setLocale(new Locale(RequestReader.getString(request, "locale")));
        setEmail(RequestReader.getString(request, "email"));
        setPhone(RequestReader.getString(request, "phone"));
        setMobile(RequestReader.getString(request, "mobile"));
        setNotes(RequestReader.getString(request, "notes"));
    }

    public boolean readUserRegistrationData(HttpServletRequest request) {
        setFirstName(RequestReader.getString(request, "firstName"));
        setLastName(RequestReader.getString(request, "lastName"));
        setEmail(RequestReader.getString(request, "email"));
        setLogin(RequestReader.getString(request, "login"));
        return true;
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "user");
        XmlUtil.addIntAttribute(xmlDoc, node, "id", getId());
        XmlUtil.addAttribute(xmlDoc, node, "firstName", StringUtil.toXml(getFirstName()));
        XmlUtil.addAttribute(xmlDoc, node, "middleName", StringUtil.toXml(getMiddleName()));
        XmlUtil.addAttribute(xmlDoc, node, "lastName", StringUtil.toXml(getLastName()));
        XmlUtil.addAttribute(xmlDoc, node, "street", StringUtil.toXml(getStreet()));
        XmlUtil.addAttribute(xmlDoc, node, "zipCode", StringUtil.toXml(getZipCode()));
        XmlUtil.addAttribute(xmlDoc, node, "city", StringUtil.toXml(getCity()));
        XmlUtil.addAttribute(xmlDoc, node, "country", StringUtil.toXml(getCountry()));
        XmlUtil.addAttribute(xmlDoc, node, "locale", StringUtil.toXml(getLocale().getLanguage()));
        XmlUtil.addAttribute(xmlDoc, node, "email", StringUtil.toXml(getEmail()));
        XmlUtil.addAttribute(xmlDoc, node, "phone", StringUtil.toXml(getPhone()));
        XmlUtil.addAttribute(xmlDoc, node, "mobile", StringUtil.toXml(getMobile()));
        XmlUtil.addAttribute(xmlDoc, node, "notes", StringUtil.toXml(getNotes()));
        XmlUtil.addAttribute(xmlDoc, node, "login", StringUtil.toXml(getLogin()));
        XmlUtil.addBooleanAttribute(xmlDoc, node, "approved", isApproved());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "locked", isLocked());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "deleted", isDeleted());
        Element groupsNode = XmlUtil.addNode(xmlDoc, node, "groups");
        for (Integer gid : groupIds) {
            Element groupNode = XmlUtil.addNode(xmlDoc, groupsNode, "group");
            XmlUtil.addIntAttribute(xmlDoc, groupNode, "id", gid);
        }
    }

    @Override
    public boolean isComplete() {
        return isComplete(login) && isCompletePassword() && isComplete(email) && isComplete(lastName);
    }

    public boolean isCompleteProfile() {
        return isComplete(login) && isComplete(email) && isComplete(lastName);
    }

}
