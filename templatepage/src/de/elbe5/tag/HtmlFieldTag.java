/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.application.Statics;
import de.elbe5.templatepage.TemplatePageData;
import de.elbe5.templatepage.templatepagepart.HtmlField;
import de.elbe5.templatepage.templatepagepart.TemplatePagePartData;
import de.elbe5.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class HtmlFieldTag extends FieldTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
            TemplatePagePartData partData = (TemplatePagePartData) rdata.get(Statics.KEY_PART);

            HtmlField field = partData.ensureHtmlField(name);

            boolean pageEditMode = rdata.isPageEditMode();
            boolean partEditMode = pageEditMode && partData == pageData.getEditPagePart();
            if (partEditMode) {
                StringUtil.write(writer, "<div class=\"ckeditField\" id=\"{1}\" contenteditable=\"true\">{2}</div><input type=\"hidden\" name=\"{3}\" value=\"{4}\" /><script type=\"text/javascript\">$('#{5}').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ctrl/templatepagepart/openLinkBrowser?pageId={6}',filebrowserImageBrowseUrl : '/ctrl/templatepagepart/openImageBrowser?pageId={7}'});</script>", field.getIdentifier(), field.getContent().isEmpty() ? StringUtil.toHtml(placeholder) : field.getContent(), field.getIdentifier(), StringUtil.toHtml(field.getContent()), field.getIdentifier(), Integer.toString(pageData.getId()), Integer.toString(pageData.getId()));
            } else {
                try {
                    if (field.getContent().isEmpty()) {
                        writer.write("");
                    } else {
                        writer.write(field.getContent());
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }


}

