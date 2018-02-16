/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.file.FileData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class DocumentListControl extends TemplateInclude {

    public static final String KEY = "documents";

    private static DocumentListControl instance = null;

    public static DocumentListControl getInstance() {
        if (instance == null)
            instance = new DocumentListControl();
        return instance;
    }

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.writer;
        HttpServletRequest request=outputContext.getRequest();
        if (outputData.pageData==null)
            return;
        int siteId = outputData.pageData.getParentId();
        SiteData site = TreeCache.getInstance().getSite(siteId);
        List<FileData> files = site.getFiles();
        for (FileData file : files) {
            if (!file.isAnonymous() && !SessionReader.hasContentRight(request, file.getId(), Right.READ))
                continue;
            writer.write("<div class=\"documentListLine icn ifile\"><a href=\"{1}\" target=\"_blank\" title=\"{2}\">{3}</a></div>",
                    file.getUrl(),
                    StringUtil.getHtml("_show", outputData.locale),
                    StringUtil.toHtml(file.getDisplayName()));
        }
    }

}
