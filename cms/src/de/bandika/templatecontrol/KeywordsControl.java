/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.page.PageData;
import de.bandika.template.TemplateAttributes;

import javax.servlet.http.HttpServletRequest;

public class KeywordsControl extends TemplateControl {

    public static String KEY = "keywords";

    private static KeywordsControl instance = null;

    public static KeywordsControl getInstance() {
        if (instance == null)
            instance = new KeywordsControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request){
        sb.append("<meta name=\"keywords\" content=\"").append(StringUtil.toHtml(pageData.getKeywords())).append("\">");
    }

}
