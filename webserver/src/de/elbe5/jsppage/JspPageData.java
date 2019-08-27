/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.jsppage;

import de.elbe5.page.PageData;

public class JspPageData extends PageData {

    // jsp File
    protected String jsp = "";

    public JspPageData() {
    }

    // jsp

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    public String getInclude() {
        return getJsp();
    }


    @Override
    public String getSavePageContentScript() {
        return "\n<script type=\"text/javascript\">function savePageContent(){}</script>\n";
    }

}
