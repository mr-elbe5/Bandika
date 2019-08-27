/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.editorpage;

import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;

public class EditorPageData extends PageData {

    protected String content = "";

    public EditorPageData() {
    }

    public boolean isEditable(){
        return true;
    }

    public String getInclude() {
        return "/WEB-INF/_jsp/editorpage/pageContent.inc.jsp";
    }

    @Override
    public String getSavePageContentScript() {
        return "\n<script type=\"text/javascript\">function savePageContent(){saveEditorPage("+getId()+");}</script>\n";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void readContent(RequestData rdata){
        setContent(rdata.getString("ckedit"));
    }

}
