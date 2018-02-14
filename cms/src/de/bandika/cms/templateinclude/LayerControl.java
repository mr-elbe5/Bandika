/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;

import java.io.IOException;
import java.io.Writer;

public class LayerControl extends TemplateInclude {

    public static final String KEY = "layer";

    private static LayerControl instance = null;

    public static LayerControl getInstance() {
        if (instance == null)
            instance = new LayerControl();
        return instance;
    }

    public boolean isDynamic(){
        return false;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Writer writer=outputContext.getWriter();
        writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "treeLayer").getCode());
        writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "dialogLayer").getCode());
        if (outputData.pageData!=null && outputData.pageData.isEditMode()) {
            writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserLayer").getCode());
            writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserDialogLayer").getCode());
        }
    }

}
