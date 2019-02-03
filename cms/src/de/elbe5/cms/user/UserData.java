/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.user;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.RightBean;
import de.elbe5.cms.rights.RightsCache;
import de.elbe5.cms.servlet.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class UserData extends BaseIdData implements IRequestData {

    public static final int ID_SYSTEM = 1;

    public static int MAX_PORTRAIT_WIDTH = 200;
    public static int MAX_PORTRAIT_HEIGHT = 200;

    public static int MIN_LOGIN_LENGTH = 4;
    public static int MIN_PASSWORD_LENGTH = 8;

    protected String title = "";
    protected String firstName = "";
    protected String lastName = "";
    protected Locale locale = null;
    protected String email = "";
    protected String login = "";
    protected String passwordHash = "";
    protected String passwordKey = "";
    protected String approvalCode = "";
    protected boolean approved = false;
    protected boolean emailVerified = false;
    protected int failedLoginCount = 0;
    protected boolean locked = false;
    protected boolean deleted = false;
    protected String street = "";
    protected String zipCode = "";
    protected String city = "";
    protected String country = "";
    protected String phone = "";
    protected String fax = "";
    protected String mobile = "";
    protected String notes = "";
    protected String portraitName = "";
    protected byte[] portrait = null;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean hasPassword() {
        return !passwordHash.isEmpty() && !passwordKey.isEmpty();
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public void setPassword(String password){
        if (password.isEmpty()){
            setPasswordHash("");
            setPasswordKey("");
        }
        else{
            String key = UserSecurity.generateKey();
            setPasswordHash(UserSecurity.encryptPassword(password, key));
            setPasswordKey(key);
        }
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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

    protected Set<Integer> groupIds = new HashSet<>();

    protected List<GroupData> groups = new ArrayList<>();

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public String getPortraitName() {
        return portraitName;
    }

    public void setPortraitName(String portraitName) {
        this.portraitName = portraitName;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
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

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public List<GroupData> getGroups() {
        return groups;
    }

    private void readBasicData(HttpServletRequest request) {
        setTitle(RequestReader.getString(request, "title"));
        setFirstName(RequestReader.getString(request, "firstName"));
        setLastName(RequestReader.getString(request, "lastName"));
        setLocale(new Locale(RequestReader.getString(request, "locale")));
        setEmail(RequestReader.getString(request, "email"));
        setStreet(RequestReader.getString(request, "street"));
        setZipCode(RequestReader.getString(request, "zipCode"));
        setCity(RequestReader.getString(request, "city"));
        setCountry(RequestReader.getString(request, "country"));
        setPhone(RequestReader.getString(request, "phone"));
        setFax(RequestReader.getString(request, "fax"));
        setMobile(RequestReader.getString(request, "mobile"));
        setNotes(RequestReader.getString(request, "notes"));
        BinaryFileData file = RequestReader.getFile(request, "portrait");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            try {
                BufferedImage source = ImageUtil.createImage(file.getBytes(), file.getContentType());
                if (source != null) {
                    float factor = ImageUtil.getResizeFactor(source, MAX_PORTRAIT_WIDTH, MAX_PORTRAIT_HEIGHT);
                    BufferedImage image = ImageUtil.copyImage(source, factor);
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                    ImageWriter writer = writers.next();
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
                    writer.setOutput(ios);
                    writer.write(image);
                    bout.flush();
                    bout.close();
                    setPortrait(bout.toByteArray());
                    setPortraitName(file.getFileName());
                }
            } catch (IOException e) {
                Log.error("could not create portrait", e);
            }
        }
    }

    private void checkBasics(RequestError error){
        if (lastName.isEmpty())
            error.addErrorField("lastName");
        if (email.isEmpty())
            error.addErrorField("email");
    }

    public boolean readRequestData(HttpServletRequest request) {
        readBasicData(request);
        setLogin(RequestReader.getString(request, "login"));
        setPassword(RequestReader.getString(request, "password"));
        setApproved(RequestReader.getBoolean(request, "approved"));
        setEmailVerified(RequestReader.getBoolean(request, "emailVerified"));
        setGroupIds(RequestReader.getIntegerSet(request, "groupIds"));
        RequestError error= new RequestError();
        if (login.isEmpty())
            error.addErrorField("login");
        if (!isNew() && !hasPassword())
            error.addErrorField("password");
        checkBasics(error);
        if (!error.isEmpty()) {
            error.setError(request);
            return false;
        }
        return true;
    }

    public boolean readProfileRequestData(HttpServletRequest request) {
        readBasicData(request);
        RequestError error= new RequestError();
        checkBasics(error);
        if (!error.isEmpty()) {
            error.setError(request);
            return false;
        }
        return true;
    }

    public boolean readRegistrationRequestData(HttpServletRequest request) {
        readBasicData(request);
        Locale locale= SessionReader.getSessionLocale(request);
        setLogin(RequestReader.getString(request, "login"));
        String password1 = RequestReader.getString(request, "password1");
        String password2 = RequestReader.getString(request, "password2");
        RequestError error= new RequestError();
        checkBasics(error);
        if (login.isEmpty())
            error.addErrorField("login");
        if (!error.isEmpty())
            error.addErrorString(Strings._notComplete.string(locale));
        if (login.length() < UserData.MIN_LOGIN_LENGTH) {
            error.addErrorField("login");
            error.addErrorString(Strings._loginLengthError.string(locale));
        }
        if (password1.length() < UserData.MIN_PASSWORD_LENGTH) {
            error.addErrorField("password1");
            error.addErrorString(Strings._passwordLengthError.string(locale));
        }
        else if (!password1.equals(password2)) {
            error.addErrorField("password2");
            error.addErrorString(Strings._passwordsDontMatch.string(locale));
        }
        else
            setPassword(password1);
        if (!error.isEmpty()) {
            error.setError(request);
            return false;
        }
        return true;
    }

}
