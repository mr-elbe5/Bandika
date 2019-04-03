/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.page.*;
import de.elbe5.cms.servlet.RequestData;

import javax.servlet.http.HttpServletRequest;

public class SectionTag extends BaseTag {

    private String name="";
    private boolean flex=false;

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
            PageData pageData = (PageData) rdata.get(Statics.KEY_PAGE);
            SectionData sectionData = pageData.ensureSection(name);
            if (sectionData != null) {
                sectionData.setFlex(flex);
                rdata.put("sectionData",sectionData);
                String url;
                if (pageData.getViewMode()== ViewMode.EDIT) {
                    if (sectionData.getParts().isEmpty())
                        url="/WEB-INF/_jsp/page/editEmptySection.inc.jsp";
                    else if (pageData.getEditPagePart() == null)
                        url = "/WEB-INF/_jsp/page/editSection.inc.jsp";
                    else
                        url = "/WEB-INF/_jsp/page/editPartInSection.inc.jsp";
                } else {
                    url = "/WEB-INF/_jsp/page/section.inc.jsp";
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
