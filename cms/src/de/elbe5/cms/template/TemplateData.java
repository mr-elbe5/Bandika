/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.data.BaseData;
import de.elbe5.cms.application.ApplicationPath;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.io.Serializable;


public class TemplateData extends BaseData implements IRequestData, Serializable {

    public static final String TYPE_MASTER = "MASTER";
    public static final String TYPE_PAGE = "PAGE";
    public static final String TYPE_PART = "PART";

    public static final String JSP_HEAD = "" +
            "<%response.setContentType(\"text/html;charset=UTF-8\");%>\n" +
            "<%@ page trimDirectiveWhitespaces=\"true\" %>" +
            "<%@ taglib uri=\"/WEB-INF/cmstags.tld\" prefix=\"cms\" %>\n";

    public static String getTemplatePath(String type, String name) {
        return ApplicationPath.getAppROOTPath() + "/WEB-INF/_jsp/_templates/" + type + "/" + name + ".jsp";
    }

    public static String getTemplateUrl(String type, String name) {
        return "/WEB-INF/_jsp/_templates/" + type + "/" + name + ".jsp";
    }

    protected String type = "";
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String code = "";

    public TemplateData() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return getTemplatePath(getType(), getName());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getJspCode() {
        return JSP_HEAD + code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void readRequestData(RequestData rdata) {
        if (isNew())
            setName(rdata.getString("name"));
        setDisplayName(rdata.getString("displayName", name));
        setDescription(rdata.getString("description"));
        setCode(rdata.getString("code"));
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
        if (code.isEmpty()) {
            rdata.addIncompleteField("code");
        }
    }

}
