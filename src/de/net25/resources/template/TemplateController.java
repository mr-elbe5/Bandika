/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.template;

import de.net25.base.controller.*;
import de.net25.base.exception.RightException;
import de.net25.base.RequestError;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

/**
 * Class TemplateController is the controller class for paragraph templates. <br>
 * Usage:
 */
public class TemplateController extends Controller {

  public static final String pageTemplateEditAllJsp = "/jsps/resources/template/editTemplates.jsp";
  public static final String pageTemplateEditJsp = "/jsps/resources/template/editTemplate.jsp";
  public static final String pageTemplateDeleteJsp = "/jsps/resources/template/delete.jsp";

  /**
   * Method getTemplateBean returns the templateBean of this TemplateController object.
   *
   * @return the templateBean (type TemplateBean) of this TemplateController object.
   */
  public TemplateBean getTemplateBean() {
    return (TemplateBean) Statics.getBean(Statics.KEY_TEMPLATE);
  }

  /**
   * Method doMethod
   *
   * @param method of type String
   * @param rdata  of type RequestData
   * @param sdata  of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (!sdata.isEditor())
      throw new RightException();
    if ("openEditTemplates".equals(method)) return openEditTemplates(sdata);
    if ("openCreate".equals(method)) return openCreate(sdata);
    if ("openEdit".equals(method)) return openEdit(rdata, sdata);
    if ("save".equals(method)) return save(rdata, sdata);
    if ("openDelete".equals(method)) return openDelete(rdata, sdata);
    if ("delete".equals(method)) return delete(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method openEditTemplates
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditTemplates(SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("templateAdministration", sdata.getLocale()), "", pageTemplateEditAllJsp);
  }

  /**
   * Method openCreate
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openCreate(SessionData sdata) throws Exception {
    TemplateData data = new TemplateData();
    data.setId(getTemplateBean().getNextId());
    data.setBeingCreated(true);
    sdata.setParam("templateData", data);
    return new PageResponse(Strings.getString("editTemplate", sdata.getLocale()), "", pageTemplateEditJsp);
  }

  /**
   * Method openEdit
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEdit(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("tid");
    if (id == 0) {
      rdata.setError(new RequestError(Strings.getString("err_no_selection", sdata.getLocale())));
      return openEditTemplates(sdata);
    }
    TemplateData data = getTemplateBean().getTemplate(id);
    if (data == null) {
      return null;
    }
    sdata.setParam("templateData", data);
    return new PageResponse(Strings.getString("editTemplate", sdata.getLocale()), "", pageTemplateEditJsp);
  }

  /**
   * Method save
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response save(RequestData rdata, SessionData sdata) throws Exception {
    TemplateData data = (TemplateData) sdata.getParam("templateData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata)) {
      return new PageResponse(Strings.getString("editTemplate", sdata.getLocale()), "", pageTemplateEditJsp);
    }
    getTemplateBean().writeTemplateJsp(data);
    getTemplateBean().saveTemplate(data);
    return openEditTemplates(sdata);
  }

  /**
   * Method openDelete
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDelete(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("tid");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return openEditTemplates(sdata);
    }
    if (getTemplateBean().isTemplateInUse(id)) {
      addError(rdata, Strings.getString("err_template_in_use", sdata.getLocale()));
      return openEditTemplates(sdata);
    }
    return new PageResponse(Strings.getString("deleteTemplate", sdata.getLocale()), "", pageTemplateDeleteJsp);
  }

  /**
   * Method delete
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response delete(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("tid");
    if (id != 0)
      getTemplateBean().deleteTemplate(id);
    return openEditTemplates(sdata);
  }

}