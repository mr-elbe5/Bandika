/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.template.TemplateAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public abstract class TemplateControl {

    public String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
    }

}
