/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.log.Log;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;

public class SnippetInclude extends TemplateInclude {

    public static final String KEY = "snippet";

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) {
        TemplateData snippet = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, attributes.getString("name"));
        if (snippet != null) {
            try {
                snippet.writeTemplate(outputContext, outputData);
            } catch (Exception e) {
                Log.error("error in snippet template", e);
            }
        }
    }

}
