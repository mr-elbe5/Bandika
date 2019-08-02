/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.workflowpagepart;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkflowTaskData extends BaseIdData implements IRequestData, Cloneable {

    protected int workflowId = 0;
    protected int predecessorTaskId = 0;
    protected LocalDateTime creationDate = null;
    protected LocalDateTime startDate = null;
    protected LocalDateTime dueDate = null;
    protected LocalDateTime doneDate = null;
    protected LocalDateTime approveDate = null;
    protected LocalDateTime rejectDate = null;
    protected String name = "";
    protected String notes = "";
    protected int creatorId = 0;
    protected int ownerId = 0;
    protected int approverId = 0;

    protected WorkflowTaskData predecessor = null;
    protected List<WorkflowTaskData> successors = new ArrayList<>();

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public int getPredecessorTaskId() {
        return predecessorTaskId;
    }

    public void setPredecessorTaskId(int predecessorTaskId) {
        this.predecessorTaskId = predecessorTaskId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(LocalDateTime doneDate) {
        this.doneDate = doneDate;
    }

    public LocalDateTime getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(LocalDateTime approveDate) {
        this.approveDate = approveDate;
    }

    public LocalDateTime getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(LocalDateTime rejectDate) {
        this.rejectDate = rejectDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public WorkflowTaskData getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(WorkflowTaskData predecessor) {
        this.predecessor = predecessor;
    }

    public List<WorkflowTaskData> getSuccessors() {
        return successors;
    }

    @Override
    public void readRequestData(RequestData rdata) {

    }

}
