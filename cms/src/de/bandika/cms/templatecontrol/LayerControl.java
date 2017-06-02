/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.cms.page.PageData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateType;
import de.bandika.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class LayerControl extends TemplateControl {

    public static String KEY = "layer";

    private static LayerControl instance = null;

    public static LayerControl getInstance() {
        if (instance == null)
            instance = new LayerControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        writer.write(TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, "treeLayer").getCode());
        writer.write(TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, "dialogLayer").getCode());
        if (pageData!=null && pageData.isEditMode()) {
            writer.write(TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, "browserLayer").getCode());
            writer.write(TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, "browserDialogLayer").getCode());
        }
    }

}
