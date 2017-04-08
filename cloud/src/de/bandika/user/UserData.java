/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.group.GroupData;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserData extends UserLoginData {

    public static final int ID_SYSTEM = 1;

    protected String street = "";
    protected String zipCode = "";
    protected String city = "";
    protected String country = "";
    protected String phone = "";
    protected String mobile = "";
    protected String notes = "";
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
        readLoginRequestData(request);
        setStreet(RequestReader.getString(request, "street"));
        setZipCode(RequestReader.getString(request, "zipCode"));
        setCity(RequestReader.getString(request, "city"));
        setCountry(RequestReader.getString(request, "country"));
        setPhone(RequestReader.getString(request, "phone"));
        setMobile(RequestReader.getString(request, "mobile"));
        setNotes(RequestReader.getString(request, "notes"));
    }

    public void addAtributesXml(Document xmlDoc, Element node) {
        super.addAttributesXml(xmlDoc, node);
        XmlUtil.addAttribute(xmlDoc, node, "street", StringUtil.toXml(getStreet()));
        XmlUtil.addAttribute(xmlDoc, node, "zipCode", StringUtil.toXml(getZipCode()));
        XmlUtil.addAttribute(xmlDoc, node, "city", StringUtil.toXml(getCity()));
        XmlUtil.addAttribute(xmlDoc, node, "country", StringUtil.toXml(getCountry()));
        XmlUtil.addAttribute(xmlDoc, node, "phone", StringUtil.toXml(getPhone()));
        XmlUtil.addAttribute(xmlDoc, node, "mobile", StringUtil.toXml(getMobile()));
        XmlUtil.addAttribute(xmlDoc, node, "notes", StringUtil.toXml(getNotes()));
        Element groupsNode = XmlUtil.addNode(xmlDoc, node, "groups");
        for (Integer gid : groupIds) {
            Element groupNode = XmlUtil.addNode(xmlDoc, groupsNode, "group");
            XmlUtil.addIntAttribute(xmlDoc, groupNode, "id", gid);
        }
    }

}