/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.issuepagepart;

import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageFactory;
import de.elbe5.cms.request.ForwardActionResult;
import de.elbe5.cms.request.IActionResult;
import de.elbe5.cms.request.PageActionResult;
import de.elbe5.cms.request.RequestData;
import de.elbe5.cms.servlet.Controller;

public class IssuePagePartController extends Controller {

    public static final String KEY = "project";

    private static IssuePagePartController instance = new IssuePagePartController();

    public static IssuePagePartController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openProjects(RequestData rdata) {
        return showProjects();
    }

    protected IActionResult showProjects() {
        PageData pageData = PageFactory.getPageData("PageData");
        pageData.setJsp("/WEB-INF/_jsp/project/projects.jsp");
        return new PageActionResult(pageData);
    }

    public IActionResult showProjectPage() {
        return new ForwardActionResult("/WEB-INF/_jsp/project/project.jsp");
    }

    public IActionResult showProjectEditPage() {
        return new ForwardActionResult("/WEB-INF/_jsp/project/editProject.ajax.jsp");
    }

}
