/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;


import de.bandika.cms.HtmlField;
import de.bandika.data.StringCache;
import de.bandika.data.StringFormat;
import de.bandika.page.PageData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.SessionData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class HtmlTag extends CmsBaseTag {

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
            "  customConfig : '/_statics/script/editorConfig.js'," +
            "  toolbar: '%s'," +
            "  filebrowserBrowseUrl : '/page.srv?act=openSelectAsset&assetUsage=LINK&forHTML=1&activeType=page&availableTypes=page,document,image&pageId=%s'," +
            "  filebrowserImageBrowseUrl : '/page.srv?act=openSelectAsset&assetUsage=FILE&forHTML=1&activeType=image&availableTypes=image&pageId=%s'" +
            "});" +
            "" +
            "</script>";

    @Override
    protected void doEditTag(RequestData rdata) throws JspException {
        SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
        PageData data = (PageData) sdata.get("pageData");
        int pageId = data == null ? 0 : data.getId();
        field = (HtmlField) pdata.ensureField(name, HtmlField.FIELDTYPE_HTML);
        String html = field.getHtml().trim();
        if (html.isEmpty())
            html = StringCache.getHtml("cms_dummyText", sdata.getLocale());
        try {
            JspWriter writer = getWriter();
            writer.print(String.format(editTag,
                    field.getIdentifier(),
                    html,
                    field.getIdentifier(),
                    StringFormat.toHtml(html),
                    field.getIdentifier(),
                    toolbar,
                    pageId,
                    pageId
            ));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doRuntimeTag(RequestData rdata) throws JspException {
        field = (HtmlField) pdata.ensureField(name, HtmlField.FIELDTYPE_HTML);
        try {
            JspWriter writer = getWriter();
            if (field.getHtml().length() == 0)
                writer.print("&nbsp;");
            else
                writer.print(field.getHtmlForOutout());
        } catch (Exception ignored) {
        }
    }

}
