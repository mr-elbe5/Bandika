/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.cms.page.PageData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class LayerControl extends TemplateControl {

    public static final String KEY = "layer";

    private static LayerControl instance = null;

    public static LayerControl getInstance() {
        if (instance == null)
            instance = new LayerControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "treeLayer").getCode());
        writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "dialogLayer").getCode());
        if (pageData!=null && pageData.isEditMode()) {
            writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserLayer").getCode());
            writer.write(TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, "browserDialogLayer").getCode());
        }
    }

}
