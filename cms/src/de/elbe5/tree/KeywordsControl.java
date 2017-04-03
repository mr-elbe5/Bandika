/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tree;

import de.elbe5.base.util.StringUtil;
import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.template.TemplateAttributes;
import de.elbe5.template.TemplateControl;

import javax.servlet.http.HttpServletRequest;

public class KeywordsControl extends TemplateControl {

    private static KeywordsControl instance = null;

    public static KeywordsControl getInstance() {
        if (instance == null)
            instance = new KeywordsControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request){
        sb.append(StringUtil.toHtml(pageData.getKeywords()));
    }

}
