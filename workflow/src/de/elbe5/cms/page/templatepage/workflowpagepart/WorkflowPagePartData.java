/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.workflowpagepart;

import de.elbe5.cms.page.templatepage.PagePartData;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowPagePartData extends PagePartData implements IRequestData {


    protected LocalDateTime dueDate = null;
    protected String projectName = "";
    protected String notes = "";
    protected int ownerId = 0;

    Map<Integer, WorkflowTaskData> tasks = new HashMap<>();
    List<WorkflowTaskData> startTasks = new ArrayList<>();

    public String getJspPath(){
        return jspBasePath + "/workflowpagepart";
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNotes() {
        return notes;
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

    public Map<Integer, WorkflowTaskData> getTasks() {
        return tasks;
    }

    public List<WorkflowTaskData> getStartTasks() {
        return startTasks;
    }

    public void organizeTasks() {
        for (WorkflowTaskData task : tasks.values()) {
            if (task.predecessorTaskId == 0)
                startTasks.add(task);
            else {
                WorkflowTaskData pretask = tasks.get(task.predecessorTaskId);
                if (pretask != null) {
                    task.setPredecessor(pretask);
                    pretask.getSuccessors().add(task);
                }
            }
        }
    }

    @Override
    public void setCreateValues(RequestData rdata){

    }

    @Override
    public void readRequestData(RequestData rdata) {

    }

}
