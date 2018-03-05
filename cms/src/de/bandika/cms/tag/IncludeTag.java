/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateInclude;
import de.bandika.webbase.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;

public class IncludeTag extends BaseTag {

    private String type = "";
    private String name = "";

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            PageOutputContext outputContext=(PageOutputContext)request.getAttribute("context");
            PageOutputData outputData=(PageOutputData)request.getAttribute("data");
            TemplateInclude include= TemplateCache.getTemplateInclude(type);
            if (include!=null) {
                TagAttributes attributes = new TagAttributes();
                attributes.put("name", name);
                include.setAttributes(attributes);
                try {
                    include.writeHtml(outputContext, outputData);
                } catch (Exception e) {
                    Log.error("could not write page html", e);
                }
            }
        } catch (Exception e) {
            Log.error("could not write page tag", e);
        }
        return EVAL_BODY_INCLUDE;
    }

}
