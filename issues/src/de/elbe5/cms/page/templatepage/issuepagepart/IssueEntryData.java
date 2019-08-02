/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.issuepagepart;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.util.ArrayList;
import java.util.List;

import static de.elbe5.cms.page.templatepage.issuepagepart.IssueData.STATE_NONE;

public class IssueEntryData extends BaseIdData implements IRequestData {

    protected int issueId = 0;
    protected String workState = STATE_NONE;
    protected String entry = "";
    protected int creatorId = 0;
    protected int assigneeId = 0;

    List<IssueFileData> files = new ArrayList<>();

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    public List<IssueFileData> getFiles() {
        return files;
    }

    @Override
    public void readRequestData(RequestData rdata) {

    }

}
