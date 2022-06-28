/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.data.IJsonDataPackage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class UserCache implements IJsonDataPackage {

    private static final UserCache instance = new UserCache();

    public static UserCache getInstance() {
        return instance;
    }

    private int version = 1;
    private boolean dirty = true;
    private static final Integer lockObj = 1;

    private Map<Integer, UserData> userMap = new HashMap<>();
    private Map<Integer, GroupData> groupMap = new HashMap<>();

    private Map<Integer, CompanyData> companyMap = new HashMap<>();

    public synchronized void load() {
        UserBean userBean = UserBean.getInstance();
        List<UserData> userList = userBean.getAllUsers();
        Map<Integer, UserData> users = new HashMap<>();
        for (UserData user : userList) {
            users.put(user.getId(), user);
        }
        userMap = users;
        GroupBean groupBean = GroupBean.getInstance();
        List<GroupData> groupList = groupBean.getAllGroups();
        Map<Integer, GroupData> groups = new HashMap<>();
        for (GroupData group : groupList) {
            groups.put(group.getId(), group);
        }
        groupMap = groups;
        CompanyBean bean = CompanyBean.getInstance();
        List<CompanyData> companyList = bean.getAllCompanies();
        Map<Integer, CompanyData> companies = new HashMap<>();
        for (CompanyData company : companyList) {
            companies.put(company.getId(), company);
        }
        companyMap = companies;
    }

    public void setDirty() {
        increaseVersion();
        dirty = true;
    }

    public void checkDirty() {
        if (dirty) {
            synchronized (lockObj) {
                if (dirty) {
                    load();
                    dirty = false;
                }
            }
        }
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    public UserData getUser(int id) {
        checkDirty();
        return userMap.get(id);
    }

    public GroupData getGroup(int id) {
        checkDirty();
        return groupMap.get(id);
    }

    public CompanyData getCompany(int id) {
        checkDirty();
        return companyMap.get(id);
    }

    @Override
    public String getName() {
        return "userData";
    }

    @Override
    public JSONObject saveAsJson() {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        for (UserData user : userMap.values()){
            array.put(user.toJSONObject());
        }
        jsonObject.put("users", array);
        array = new JSONArray();
        for (GroupData group : groupMap.values()){
            array.put(group.toJSONObject());
        }
        jsonObject.put("groups", array);
        array = new JSONArray();
        for (CompanyData company : companyMap.values()){
            array.put(company.toJSONObject());
        }
        jsonObject.put("companies", array);
        return jsonObject;
    }

    @Override
    public void loadFromJson(JSONObject jsonObject) {
        //todo
    }
}
