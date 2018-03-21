/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template.control;

import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class FileListControl extends TemplateControl {

    public static final String KEY = "files";

    public FileListControl(){
    }

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        if (outputData.getPageData()==null)
            return;
        int siteId = outputData.getPageData().getParentId();
        SiteData site = TreeCache.getInstance().getSite(siteId);
        List<FileData> files;
        String filter=attributes.getString("filter");
        switch (filter){
            case "documents":
                files=site.getDocuments();
                break;
            case "images":
                files=site.getImages();
                break;
            default:
                files=site.getFiles();
                break;
        }
        for (FileData file : files) {
            if (!file.isAnonymous() && !SessionReader.hasContentRight(request, file.getId(), Right.READ))
                continue;
            writer.write("<div class=\"documentListLine icn ifile\"><a href=\"{1}\" target=\"_blank\" title=\"{2}\">{3}</a></div>",
                    file.getUrl(),
                    StringUtil.getHtml("_show", outputData.getLocale()),
                    StringUtil.toHtml(file.getDisplayName()));
        }
    }

}
