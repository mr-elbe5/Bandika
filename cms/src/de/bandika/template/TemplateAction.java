/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika.base.data.BinaryFileData;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

public enum TemplateAction implements IAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    },
    /**
     * opens dialog for importing templates
     */
    openImportTemplates {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            return showImportTemplates(request, response);
        }
    },
    /**
     * imports template to database
     */
    importTemplates {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            String html = "";
            BinaryFileData file = RequestReader.getFile(request, "file");
            if (file != null && file.getBytes() != null) {
                html = new String(file.getBytes());
            }
            if (!importTemplates(html)){
                addError(request, "could not import templates");
                return showImportTemplates(request, response);
            }
            TemplateCache.getInstance().setDirty();
            return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration" , "_templatesImported");
        }
    },
    /**
     * opens dialog for creating a new template
     */
    openCreateTemplate {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            TemplateType type=TemplateType.valueOf(RequestReader.getString(request,"templateType"));
            TemplateData data = type.getNewTemplateData();
            data.setNew(true);
            data.prepareEditing();
            SessionWriter.setSessionObject(request, "templateData", data);
            return showEditTemplate(request, response);
        }
    },
    /**
     * shows template properties
     */
    showTemplateDetails {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            return showTemplateDetails(request, response);
        }
    },
    /**
     * opens dialog for editing a template
     */
    openEditTemplate {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            TemplateType templateType= TemplateType.valueOf(RequestReader.getString(request,"templateType"));
            String templateName = RequestReader.getString(request, "templateName");
            TemplateData data = TemplateCache.getInstance().getTemplate(templateType, templateName);
            if (data == null) {
                return false;
            }
            data.prepareEditing();
            SessionWriter.setSessionObject(request, "templateData", data);
            return showEditTemplate(request, response);
        }
    },
    /**
     * saves template to database
     */
    saveTemplate {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            TemplateData data = (TemplateData) getSessionObject(request, "templateData");
            if (data.isNew())
                data.setName(RequestReader.getString(request, "name"));
            data.setDisplayName(RequestReader.getString(request, "displayName"));
            data.setDescription(RequestReader.getString(request, "description"));
            data.setCode(RequestReader.getString(request, "code"));
            data.setUsage(RequestReader.getString(request, "usage"));
            if (!isDataComplete(data, request)) {
                return showEditTemplate(request, response);
            }
            TemplateBean.getInstance().saveTemplate(data, true);
            TemplateCache.getInstance().setDirty();
            return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&templateType=" + data.getType().name()+ "&templateName=" + data.getName() , "_templateSaved");
        }
    },
    /**
     * opens dialog for deleting a master template
     */
    openDeleteTemplate {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            return showDeleteTemplate(request, response);
        }
    },
    /**
     * deletes master template from database
     */
    deleteTemplate {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                return false;
            TemplateType templateType = TemplateType.valueOf(RequestReader.getString(request, "templateType"));
            String templateName = RequestReader.getString(request, "templateName");
            TemplateBean.getInstance().deleteTemplate(templateName, templateType);
            TemplateCache.getInstance().setDirty();
            return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_templateDeleted");
        }
    };

    public static final String KEY = "template";
    public static void initialize(){
        ActionDispatcher.addClass(KEY, TemplateAction.class);
    }
    @Override
    public String getKey(){return KEY;}

    public static final String TAG_START = "<cms-template";
    public static final String TAG_END = "</cms-template>";

    public boolean importTemplates(String src) throws ParseException {
        int pos1;
        int pos2 = 0;
        try {
            while (true) {
                pos1 = src.indexOf(TAG_START, pos2);
                if (pos1 == -1) {
                    break;
                }
                pos1 += TAG_START.length();
                pos2 = src.indexOf('>', pos1);
                if (pos2 == -1)
                    throw new ParseException("no cms tag end", pos1);
                TemplateAttributes attributes = new TemplateAttributes(src.substring(pos1, pos2).trim());
                pos2++;
                pos1 = src.indexOf(TAG_END, pos2);
                if (pos1 == -1)
                    throw new ParseException("no cms end tag ", pos2);
                String content = src.substring(pos2, pos1).trim();
                pos2 = pos1 + TAG_END.length();
                if (!importTemplate(attributes, content))
                    return false;
            }
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }

    protected boolean importTemplate(TemplateAttributes attributes, String code){
        TemplateType type=TemplateType.valueOf(attributes.getString("type"));
        TemplateData data = type.getNewTemplateData();
        data.setName(attributes.getString("name"));
        data.setDisplayName(attributes.getString("displayName"));
        data.setUsage(attributes.getString("usage"));
        data.setCode(code);
        if (TemplateCache.getInstance().getTemplate(data.getType(),data.getName())==null)
            data.setNew(true);
        return TemplateBean.getInstance().saveTemplate(data, false);
    }

    public boolean showImportTemplates(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/importTemplates.ajax.jsp");
    }

    public boolean showEditTemplate(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/editTemplate.ajax.jsp");
    }

    public boolean showDeleteTemplate(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/deleteTemplate.ajax.jsp");
    }

    protected boolean showTemplateDetails(HttpServletRequest request, HttpServletResponse response) {return sendForwardResponse(request, response, "/WEB-INF/_jsp/template/templateDetails.ajax.jsp");}

}
