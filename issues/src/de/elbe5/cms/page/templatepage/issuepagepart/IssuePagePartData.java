/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.issuepagepart;

import de.elbe5.cms.application.Strings;
import de.elbe5.cms.page.templatepage.PagePartData;
import de.elbe5.cms.request.RequestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IssuePagePartData extends PagePartData {

    protected String projectName = "";
    protected String notes = "";
    protected int ownerId = 0;
    protected int groupId = 0;

    List<IssueData> issues = new ArrayList<>();

    public String getJspPath(){
        return jspBasePath + "/issuepagepart";
    }

    public String getNotes() {
        return notes;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<IssueData> getIssues() {
        return issues;
    }

    public String getEditTitle(Locale locale) {
        return Strings._issues.string(locale)+" "+projectName;
    }

    @Override
    public void setCreateValues(RequestData rdata){
        setOwnerId(rdata.getUserId());
    }

    @Override
    public void readRequestData(RequestData rdata) {
        setProjectName(rdata.getString("projectName").trim());
        setNotes(rdata.getString("notes"));
        setGroupId(rdata.getInt("groupId"));
    }

    @Override
    public void readPagePartSettingsData(RequestData rdata) {
        super.readPagePartSettingsData(rdata);
    }

}
