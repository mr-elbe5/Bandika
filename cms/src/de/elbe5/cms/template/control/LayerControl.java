/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template.control;

import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.webbase.servlet.SessionReader;

import java.io.IOException;

public class LayerControl extends TemplateControl {

    public static final String KEY = "layer";

    private static LayerControl instance = null;

    public static LayerControl getInstance() {
        if (instance == null)
            instance = new LayerControl();
        return instance;
    }

    private LayerControl(){
    }

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        if (SessionReader.isEditMode(outputContext.getRequest())) {
            writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "treeLayer").getCode());
            if (outputData.getPageData() != null && outputData.getPageData().isPageEditMode()) {
                writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserLayer").getCode());
                writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserDialogLayer").getCode());
            }
        }
        writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "dialogLayer").getCode());
    }

}
