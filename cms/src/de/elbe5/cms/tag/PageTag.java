/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;

public class PageTag extends BaseTag {

    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            PageData pageData = (PageData) request.getAttribute("pageData");
            int siteId = pageData==null ? TreeCache.getInstance().getLanguageRootId(SessionReader.getSessionLocale(request)) : pageData.getParentId();
            String templateName = TreeCache.getInstance().getSite(siteId).getTemplateName();
            TemplateData masterTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_MASTER, templateName);
            PageOutputContext outputContext=new PageOutputContext(context);
            PageOutputData outputData=new PageOutputData(pageData, SessionReader.getSessionLocale(request));
            try {
                masterTemplate.writeTemplate(outputContext, outputData);
            } catch (Exception e) {
                Log.error("could not write page html", e);
            }
        } catch (Exception e) {
            Log.error("could not write page tag", e);
        }
        return SKIP_BODY;
    }

}