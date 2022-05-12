/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.base.BaseData;
import de.elbe5.content.ContentBean;
import de.elbe5.layout.PartHtml;
import de.elbe5.request.RequestData;

public class PagePartData extends BaseData implements Comparable<PagePartData> {

    public static final String KEY_PART = "partData";
    public static String LAYOUT_TYPE = "Part";

    protected String cssClass = "";
    protected String sectionName = "";
    protected int position = 0;
    protected boolean editable = true;

    public PagePartData() {
    }

    public void copyData(PagePartData data) {
        setId(ContentBean.getInstance().getNextId());
        setEditable((data.isEditable()));
    }

    @Override
    public int compareTo(PagePartData data) {
        return position - data.position;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPartWrapperId() {
        return "part_" + getId();
    }

    public String getPartPositionName() {
        return "partpos_" + getId();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getEditTitle() {
        return "Section Part, ID=" + getId();
    }

    public void prepareCopy() {
        setNew(true);
        setId(PagePartBean.getInstance().getNextPartId());
    }

    public void setCreateValues(RequestData rdata) {
        String sectionName = rdata.getAttributes().getString("sectionName");
        setSectionName(sectionName);
        setId(PagePartBean.getInstance().getNextPartId());
        setNew(true);
    }

    public void readFrontendRequestData(RequestData rdata) {
        // -1 if deleted
        setPosition(rdata.getAttributes().getInt(getPartPositionName(),-1));
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        PartHtml.appendPartStart(sb, this);
        appendContent(sb, rdata);
        PartHtml.appendPartEnd(sb);
    }

    public void appendEditHtml(StringBuilder sb, RequestData rdata){
        PartHtml.appendPartEditStart(sb, this);
        appendContent(sb, rdata);
        PartHtml.appendPartEnd(sb);
    }

    public void appendContent(StringBuilder sb, RequestData rdata){

    }

}
