/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.field.HtmlField;
import de.elbe5.cms.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class CmsHtmlTag extends CmsBaseTag {
    protected String toolbar = "Full";
    protected String height;
    protected HtmlField field;

    public void setToolbar(String toolbar) {
        this.toolbar = toolbar;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    private static final String editTag = "" +
            "<div class=\"ckeditField\" id=\"%s\" contenteditable=\"true\">%s</div>" +
            "<input type=\"hidden\" name=\"%s\" value=\"%s\" />" +
            "<script type=\"text/javascript\">" +
            "CKEDITOR.disableAutoInline=true;" +
            "CKEDITOR.inline('%s',{" +
            "  customConfig : '/_statics/js/editorConfig.js'," +
            "  toolbar: '%s'," +
            "  filebrowserBrowseUrl : '/field.srv?act=openLinkBrowser&siteId=%s&pageId=%s'," +
            "  filebrowserImageBrowseUrl : '/field.srv?act=openImageBrowser&siteId=%s&pageId=%s'" +
            "});" +
            "" +
            "</script>";

    @Override
    protected void doEditTag(HttpServletRequest request) throws JspException {
        PageData data = (PageData) SessionHelper.getSessionObject(request, "pageData");
        int siteId = data == null ? 0 : data.getParentId();
        int pageId = data == null ? 0 : data.getId();
        field = (HtmlField) pdata.ensureField(name, HtmlField.FIELDTYPE_HTML);
        String html = field.getHtml().trim();
        if (html.isEmpty()) html = StringUtil.getHtml("_dummyText", SessionHelper.getSessionLocale(request));
        try {
            JspWriter writer = getWriter();
            writer.print(String.format(editTag,
                    field.getIdentifier(),
                    html,
                    field.getIdentifier(),
                    StringUtil.toHtml(html),
                    field.getIdentifier(),
                    toolbar,
                    siteId,
                    pageId,
                    siteId,
                    pageId));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(HttpServletRequest request) throws JspException {
        field = (HtmlField) pdata.ensureField(name, HtmlField.FIELDTYPE_HTML);
        try {
            JspWriter writer = getWriter();
            if (field.getHtml().length() == 0) writer.print("&nbsp;");
            else writer.print(field.getHtmlForOutout());
        } catch (Exception ignored) {
        }
    }
}
