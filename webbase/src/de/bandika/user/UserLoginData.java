/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.Locales;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.rights.RightBean;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class UserLoginData extends BaseIdData {

    public static final int ID_SYSTEM = 1;

    protected String title = "";
    protected String firstName = "";
    protected String lastName = "";
    protected Locale locale = null;
    protected String email = "";
    protected String login = "";
    protected String password = "";
    protected String approvalCode = "";
    protected boolean approved = false;
    protected int failedLoginCount = 0;
    protected boolean locked = false;
    protected boolean deleted = false;

    UserRightsData rights=new UserRightsData();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        if (firstName.length() == 0) {
            return lastName;
        }
        return firstName + ' ' + lastName;
    }

    public Locale getLocale() {
        if (locale != null)
            return locale;
        return Locales.getInstance().getDefaultLocale();
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

    public UserRightsData getRights() {
        return rights;
    }

    public void setRights(UserRightsData rights) {
        this.rights = rights;
    }

    public boolean checkRights() {
        if (rights==null)
            return false;
        int ver = RightsCache.getInstance().getVersion();
        if (ver == rights.getVersion())
            return true;
        UserRightsData newRights = RightBean.getInstance().getUserRights(getId());
        newRights.setVersion(ver);
        setRights(newRights);
        return true;
    }

    public void readLoginRequestData(HttpServletRequest request) {
        setTitle(RequestReader.getString(request, "title"));
        setFirstName(RequestReader.getString(request, "firstName"));
        setLastName(RequestReader.getString(request, "lastName"));
        setLocale(new Locale(RequestReader.getString(request, "locale")));
        setEmail(RequestReader.getString(request, "email"));
        setLogin(RequestReader.getString(request, "login"));
        setPassword(RequestReader.getString(request, "password"));
        setApproved(RequestReader.getBoolean(request, "approved"));
    }

    public boolean readUserRegistrationData(HttpServletRequest request) {
        setTitle(RequestReader.getString(request, "title"));
        setFirstName(RequestReader.getString(request, "firstName"));
        setLastName(RequestReader.getString(request, "lastName"));
        setEmail(RequestReader.getString(request, "email"));
        setLogin(RequestReader.getString(request, "login"));
        return true;
    }

    public void addAttributesXml(Document xmlDoc, Element node) {
        XmlUtil.addIntAttribute(xmlDoc, node, "id", getId());
        XmlUtil.addAttribute(xmlDoc, node, "title", StringUtil.toXml(getTitle()));
        XmlUtil.addAttribute(xmlDoc, node, "firstName", StringUtil.toXml(getFirstName()));
        XmlUtil.addAttribute(xmlDoc, node, "lastName", StringUtil.toXml(getLastName()));
        XmlUtil.addAttribute(xmlDoc, node, "locale", StringUtil.toXml(getLocale().getLanguage()));
        XmlUtil.addAttribute(xmlDoc, node, "email", StringUtil.toXml(getEmail()));
        XmlUtil.addAttribute(xmlDoc, node, "login", StringUtil.toXml(getLogin()));
        XmlUtil.addBooleanAttribute(xmlDoc, node, "approved", isApproved());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "locked", isLocked());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "deleted", isDeleted());
    }

    @Override
    public boolean isComplete() {
        return isComplete(login) && isCompletePassword() && isComplete(email) && isComplete(lastName);
    }

    public boolean isCompleteProfile() {
        return isComplete(login) && isComplete(email) && isComplete(lastName);
    }

}
