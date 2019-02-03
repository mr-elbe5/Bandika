/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.HtmlField;
import de.elbe5.cms.page.*;
import de.elbe5.cms.servlet.ActionSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class HtmlFieldTag extends FieldTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            JspWriter writer = getContext().getOut();
            PageData pageData = (PageData) request.getAttribute(ActionSet.KEY_PAGE);
            PagePartData partData = (PagePartData) request.getAttribute(PagePartActions.KEY_PART);

            HtmlField field = partData.ensureHtmlField(name);

            boolean partEditMode = pageData.getViewMode()== ViewMode.EDIT && partData == pageData.getEditPagePart();
            if (partEditMode) {
                StringUtil.write(writer,"<div class=\"ckeditField\" id=\"{1}\" contenteditable=\"true\">{2}</div>" +
                                "<input type=\"hidden\" name=\"{3}\" value=\"{4}\" />" +
                                "<script type=\"text/javascript\">$('#{5}').ckeditor({" +
                                "toolbar : 'Full'," +
                                "filebrowserBrowseUrl : '/field.srv?act=openLinkBrowser&pageId={6}'," +
                                "filebrowserImageBrowseUrl : '/field.srv?act=openImageBrowser&pageId={7}'" +
                                "});" +
                                "</script>",
                        field.getIdentifier(),
                        field.getContent().isEmpty() ? StringUtil.toHtml(placeholder) : field.getContent(),
                        field.getIdentifier(),
                        StringUtil.toHtml(field.getContent()),
                        field.getIdentifier(),
                        Integer.toString(pageData.getId()),
                        Integer.toString(pageData.getId()));
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

