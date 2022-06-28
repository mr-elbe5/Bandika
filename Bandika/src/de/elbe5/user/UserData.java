/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Configuration;
import de.elbe5.companion.EncryptionCompanion;
import de.elbe5.companion.ImageCompanion;
import de.elbe5.data.*;
import de.elbe5.file.BinaryFile;
import de.elbe5.log.Log;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@AJsonClass
public class UserData extends BaseData implements IJsonData, ImageCompanion, EncryptionCompanion {

    public static final int ID_ROOT = 1;

    public static int MAX_PORTRAIT_WIDTH = 200;
    public static int MAX_PORTRAIT_HEIGHT = 200;

    public static int MIN_PASSWORD_LENGTH = 8;

    @AJsonField(baseClass = String.class)
    protected String title = "";
    @AJsonField(baseClass = String.class)
    protected String firstName = "";
    @AJsonField(baseClass = String.class)
    protected String lastName = "";
    @AJsonField(baseClass = String.class)
    protected String email = "";
    @AJsonField(baseClass = String.class)
    protected String login = "";
    @AJsonField(baseClass = String.class)
    protected String passwordHash = "";
    @AJsonField(baseClass = String.class)
    protected String token = "";
    @AJsonField(baseClass = LocalDateTime.class)
    protected LocalDateTime tokenExpiration = null;
    @AJsonField(baseClass = Boolean.class)
    protected boolean locked = false;
    @AJsonField(baseClass = Boolean.class)
    protected boolean deleted = false;
    @AJsonField(baseClass = String.class)
    protected String street = "";
    @AJsonField(baseClass = String.class)
    protected String zipCode = "";
    @AJsonField(baseClass = String.class)
    protected String city = "";
    @AJsonField(baseClass = String.class)
    protected String country = "";
    @AJsonField(baseClass = String.class)
    protected String phone = "";
    @AJsonField(baseClass = String.class)
    protected String fax = "";
    @AJsonField(baseClass = String.class)
    protected String mobile = "";
    @AJsonField(baseClass = String.class)
    protected String notes = "";
    @AJsonField(baseClass = Boolean.class)
    protected boolean hasPortrait=false;
    protected byte[] portrait = null;
    @AJsonField(baseClass = Integer.class)
    protected int companyId = 0;

    @AJsonField(baseClass = HashSet.class, valueClass = Integer.class)
    protected Set<Integer> groupIds = new HashSet<>();

    @AJsonField(baseClass = HashSet.class, valueClass = SystemZone.class)
    protected Set<SystemZone> systemRights = new HashSet<>();

    // base data

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
        return !passwordHash.isEmpty();
    }

    public void setPassword(String password) {
        if (password.isEmpty()) {
            setPasswordHash("");
        } else {
            setPasswordHash(encryptPassword(password, Configuration.getInstance().getSalt()));
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
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

    public boolean hasPortrait() {
        return hasPortrait;
    }

    public void setHasPortrait(boolean hasPortrait) {
        this.hasPortrait = hasPortrait;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void clearSystemRights(){
        systemRights.clear();
    }

    public void addSystemRight(SystemZone zone) {
        systemRights.add(zone);
    }

    public boolean hasAnySystemRight() {
        return !systemRights.isEmpty() || isRoot();
    }

    public boolean hasAnyAdministrationRight() {
        //more than global read right;
        return hasAnySystemRight() && !(systemRights.size() == 1 && hasSystemRight(SystemZone.CONTENTREAD));
    }

    public boolean hasSystemRight(SystemZone zone) {
        return systemRights.contains(zone) || isRoot();
    }

    public boolean hasAnyContentRight() {
        return hasSystemRight(SystemZone.CONTENTEDIT) || hasSystemRight(SystemZone.CONTENTAPPROVE);
    }

    public boolean isRoot(){
        return getId()== ID_ROOT;
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

    // multiple data

    private void readBasicData(RequestData rdata) {
        setCompanyId(rdata.getAttributes().getInt("companyId"));
        setTitle(rdata.getAttributes().getString("title"));
        setFirstName(rdata.getAttributes().getString("firstName"));
        setLastName(rdata.getAttributes().getString("lastName"));
        setEmail(rdata.getAttributes().getString("email"));
        setStreet(rdata.getAttributes().getString("street"));
        setZipCode(rdata.getAttributes().getString("zipCode"));
        setCity(rdata.getAttributes().getString("city"));
        setCountry(rdata.getAttributes().getString("country"));
        setPhone(rdata.getAttributes().getString("phone"));
        setFax(rdata.getAttributes().getString("fax"));
        setMobile(rdata.getAttributes().getString("mobile"));
        setNotes(rdata.getAttributes().getString("notes"));
        BinaryFile file = rdata.getAttributes().getFile("portrait");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !isNullOrEmpty(file.getContentType())) {
            try {
                BufferedImage source = createImage(file.getBytes(), file.getContentType());
                if (source != null) {
                    float factor = getResizeFactor(source, MAX_PORTRAIT_WIDTH, MAX_PORTRAIT_HEIGHT, true);
                    BufferedImage image = copyImage(source, factor);
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                    ImageWriter writer = writers.next();
                    setPortrait(writeImage(writer, image));
                }
            } catch (IOException e) {
                Log.error("could not create portrait", e);
            }
        }
    }

    private void checkBasics(RequestData rdata) {
        if (lastName.isEmpty())
            rdata.addIncompleteField("lastName");
        if (email.isEmpty())
            rdata.addIncompleteField("email");
    }

    public void readSettingsRequestData(RequestData rdata) {
        readBasicData(rdata);
        setLogin(rdata.getAttributes().getString("login"));
        setPassword(rdata.getAttributes().getString("password"));
        setGroupIds(rdata.getAttributes().getIntegerSet("groupIds"));
        setLocked(rdata.getAttributes().getBoolean("locked"));
        if (login.isEmpty())
            rdata.addIncompleteField("login");
        if (isNew() && !hasPassword())
            rdata.addIncompleteField("password");
        checkBasics(rdata);
    }

    public void readProfileRequestData(RequestData rdata) {
        readBasicData(rdata);
        checkBasics(rdata);
    }

}
