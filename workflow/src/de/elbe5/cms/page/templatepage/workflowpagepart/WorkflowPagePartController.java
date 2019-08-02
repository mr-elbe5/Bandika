/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.workflowpagepart;

import de.elbe5.cms.servlet.Controller;

public class WorkflowPagePartController extends Controller {

    public static final String KEY = "project";

    private static WorkflowPagePartController instance = new WorkflowPagePartController();

    public static WorkflowPagePartController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }


}
