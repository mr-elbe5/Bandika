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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssueData extends BaseIdData implements IRequestData {

    public static final String STATE_NONE = "NONE";
    public static final String STATE_OPEN = "OPEN";
    public static final String STATE_DONE = "DONE";
    public static final String STATE_CLOSED = "CLOSED";

    protected int partId = 0;
    protected int creatorId = 0;
    protected int assigneeId = 0;
    protected LocalDateTime dueDate = null;
    protected LocalDateTime closeDate = null;
    protected String workState = STATE_NONE;
    protected String title = "";
    protected String issue = "";
    protected String description = "";

    List<IssueEntryData> entries = new ArrayList<>();

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
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

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IssueEntryData> getEntries() {
        return entries;
    }

    @Override
    public void readRequestData(RequestData rdata) {

    }

}
