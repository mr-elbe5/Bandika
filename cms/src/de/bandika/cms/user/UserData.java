/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.user;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.base.util.ImageUtil;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.group.GroupData;
import de.bandika.servlet.RequestReader;
import de.bandika.user.UserLoginData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class UserData extends UserLoginData {

    public static final int ID_SYSTEM = 1;

    public static int MAX_PORTRAIT_WIDTH = 200;
    public static int MAX_PORTRAIT_HEIGHT = 200;

    public static int MIN_PASSWORD_LENGTH = 8;

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

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public List<GroupData> getGroups() {
        return groups;
    }

    public void readUserRequestData(HttpServletRequest request) {
        readUserProfileRequestData(request);
        setLogin(RequestReader.getString(request, "login"));
        setPassword(RequestReader.getString(request, "password"));
        setApproved(RequestReader.getBoolean(request, "approved"));
        setGroupIds(RequestReader.getIntegerSet(request, "groupIds"));
    }

    public void readUserProfileRequestData(HttpServletRequest request) {
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

}
