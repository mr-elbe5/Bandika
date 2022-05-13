/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.Strings;
import de.elbe5.html.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IResponse;
import de.elbe5.response.RedirectResponse;

public class LinkData extends ContentData {

    // link data
    private String linkUrl = "";
    private String linkIcon = "";

    public LinkData() {
    }

    public void copyData(LinkData data, RequestData rdata) {
        super.copyData(data, rdata);
        linkUrl = data.linkUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkIcon() {
        return linkIcon;
    }

    public void setLinkIcon(String linkIcon) {
        this.linkIcon = linkIcon;
    }

    @Override
    public void readRequestData(RequestData rdata) {
        super.readRequestData(rdata);
        setLinkUrl(rdata.getAttributes().getString("linkUrl"));
        setLinkIcon(rdata.getAttributes().getString("linkIcon"));
    }

    // html

    @Override
    public String getNavDisplay() {
        if (!linkIcon.isEmpty()) {
            return Strings.format("""
                                <img src="/static-content/img/{1}" class="navIcon" title="{2}" alt="{3}" />
                            """,
                    linkIcon,
                    Strings.toHtml(getDisplayName()),
                    Strings.toHtml(getDisplayName()));
        }
        return Strings.toHtml(getDisplayName());
    }

    @Override
    public IResponse getResponse() {
        return new RedirectResponse(linkUrl);
    }

    @Override
    public ModalPage getContentDataPage() {
        return new EditLinkDataPage();
    }

}
