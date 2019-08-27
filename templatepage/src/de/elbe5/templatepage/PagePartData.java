/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.page.PageBean;
import de.elbe5.request.IRequestData;
import de.elbe5.request.RequestData;

import java.util.Locale;
import java.util.Set;

public abstract class PagePartData extends BaseIdData implements IRequestData, Comparable<PagePartData> {

    protected String name = "";
    protected String sectionName = "";
    protected int ranking = 0;
    protected boolean editable = true;
    protected String flexClass = "";

    protected Set<Integer> pageIds = null;

    public static String jspBasePath = "/WEB-INF/_jsp/templatepage";

    public PagePartData() {
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setEditable((data.isEditable()));
        setFlexClass(data.getFlexClass());
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return name.compareTo(data.name);
    }

    public String getJspPath() {
        return jspBasePath;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getPartInclude() {
        return getJspPath() + "/show.jsp";
    }

    public String getEditPartInclude() {

        return getJspPath() + "/edit.jsp";
    }

    public String getSettingsInclude() {
        return getJspPath() + "/partSettings.ajax.jsp";
    }

    public String getPartWrapperId() {
        return "partWrapper_" + getId();
    }

    public String getPartWrapperJqId() {
        return "#" + getPartWrapperId();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getEditTitle(Locale locale) {
        return "Part (ID=" + getId() + ")";
    }

    public String getFlexClass() {
        return flexClass;
    }

    public void setFlexClass(String flexClass) {
        this.flexClass = flexClass;
    }

    public String getCss(boolean flex) {
        if (flex)
            return getFlexClass();
        return "";
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public void prepareCopy() {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
    }

    public void setCreateValues(RequestData rdata) {

    }

    @Override
    public void readRequestData(RequestData rdata) {
    }

    public void readPagePartSettingsData(RequestData rdata) {
        setFlexClass(rdata.getString("flexClass"));
    }

}
