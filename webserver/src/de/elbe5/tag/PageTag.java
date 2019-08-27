/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateData;

import javax.servlet.http.HttpServletRequest;

public class PageTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            PageData pageData = rdata.getCurrentPage();
            try {
                getContext().include(TemplateData.getTemplateUrl(PageData.TYPE_MASTER_TEMPLATE, pageData.getMasterName()));
            } catch (Exception e) {
                Log.error("could not write page html", e);
            }
        } catch (Exception e) {
            Log.error("could not write page tag", e);
        }
        return SKIP_BODY;
    }

}
