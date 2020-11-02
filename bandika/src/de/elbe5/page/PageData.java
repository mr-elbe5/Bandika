/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.IResponse;
import de.elbe5.response.ContentResponse;
import de.elbe5.response.MasterView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class PageData extends ContentData {

    public static String MASTER_TYPE = "Master";

    private String keywords = "";
    protected String master = MasterView.DEFAULT_MASTER;
    protected LocalDateTime publishDate = null;
    protected String publishedContent="";

    // base data

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate=publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public void reformatPublishedContent() {
        Document doc= Jsoup.parseBodyFragment(getPublishedContent());
        setPublishedContent(doc.body().html());
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return getPublishDate() != null;
    }

    // view

    public IResponse getDefaultView(){
        return new ContentResponse(this, getMaster());
    }

    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        JspWriter writer = context.getOut();
        switch (getViewType()) {
            case VIEW_TYPE_PUBLISH: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                StringWriter stringWriter = new StringWriter();
                context.pushBody(stringWriter);
                displayDraftContent(context, context.getOut(), rdata);
                setPublishedContent(stringWriter.toString());
                reformatPublishedContent();
                context.popBody();
                //Log.log("publishing page " + getDisplayName());
                if (!PageBean.getInstance().publishPage(this)) {
                    Log.error("error writing published content");
                }
                writer.write(getPublishedContent());
                setViewType(ContentData.VIEW_TYPE_SHOW);
                ContentCache.setDirty();
                writer.write("</div>");
            }
            break;
            case VIEW_TYPE_EDIT: {
                writer.write("<div id=\"pageContent\" class=\"editArea\">");
                displayEditContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
            case VIEW_TYPE_SHOWPUBLISHED: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                if (isPublished())
                    displayPublishedContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
            default: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                if (isPublished() && !hasUserEditRight(rdata))
                    displayPublishedContent(context, context.getOut(), rdata);
                else
                    displayDraftContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
        }
    }

    protected void displayEditContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
    }

    protected void displayDraftContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
    }

    protected void displayPublishedContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        writer.write(publishedContent);
    }

    // multiple data

    public void copyData(ContentData data, SessionRequestData rdata) {
        if (!(data instanceof PageData))
            return;
        PageData hcdata=(PageData)data;
        super.copyData(hcdata,rdata);
        setKeywords(hcdata.getKeywords());
        setMaster(hcdata.getMaster());
    }

    @Override
    public void readRequestData(SessionRequestData rdata) {
        super.readRequestData(rdata);
        setKeywords(rdata.getString("keywords"));
        setMaster(rdata.getString("master"));
    }

    public void readFrontendRequestData(SessionRequestData rdata) {
    }

}
