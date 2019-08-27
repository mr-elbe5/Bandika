/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.templatepage.SectionData;
import de.elbe5.templatepage.TemplatePageData;
import de.elbe5.request.RequestData;

import javax.servlet.http.HttpServletRequest;

public class SectionTag extends BaseTag {

    private String name = "";
    private boolean flex = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setFlex(boolean flex) {
        this.flex = flex;
    }

    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
            SectionData sectionData = pageData.ensureSection(name);
            if (sectionData != null) {
                sectionData.setFlex(flex);
                rdata.put("sectionData", sectionData);
                String url;
                if (rdata.isPageEditMode()) {
                    if (sectionData.getParts().isEmpty())
                        url = "/WEB-INF/_jsp/templatepage/editEmptySection.inc.jsp";
                    else if (pageData.getEditPagePart() == null)
                        url = "/WEB-INF/_jsp/templatepage/editSection.inc.jsp";
                    else
                        url = "/WEB-INF/_jsp/templatepage/editPartInSection.inc.jsp";
                } else {
                    url = "/WEB-INF/_jsp/templatepage/section.inc.jsp";
                }
                getContext().include(url);
                sectionData.setFlex(false);
                request.removeAttribute("sectionData");
            }
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}
