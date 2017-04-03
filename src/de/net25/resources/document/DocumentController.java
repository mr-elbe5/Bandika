/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.document;

import de.net25.base.resources.FileData;
import de.net25.base.Logger;
import de.net25.base.exception.RightException;
import de.net25.base.controller.*;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

/**
 * Class DocumentController is the controller class for document files. <br>
 * Usage:
 */
public class DocumentController extends Controller {

  public static final String documentEditAllJsp = "/jsps/resources/document/editDocuments.jsp";
  public static final String documentSelectJsp = "/jsps/resources/document/select.jsp";
  public static final String documentUploadJsp = "/jsps/resources/document/upload.jsp";
  public static final String documentUploadSelectJsp = "/jsps/resources/document/uploadSelect.jsp";
  public static final String documentUploadOkJsp = "/jsps/resources/document/uploadOk.jsp";
  public static final String documentUploadSelectOkJsp = "/jsps/resources/document/uploadSelectOk.jsp";
  public static final String documentUpdateJsp = "/jsps/resources/document/update.jsp";
  public static final String documentDeleteJsp = "/jsps/resources/document/delete.jsp";
  public static final String documentDeleteAllJsp = "/jsps/resources/document/deleteAll.jsp";

  /**
   * Method getDocumentBean returns the documentBean of this DocumentController object.
   *
   * @return the documentBean (type DocumentBean) of this DocumentController object.
   */
  public DocumentBean getDocumentBean() {
    return (DocumentBean) Statics.getBean(Statics.KEY_DOCUMENT);
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
    if ("show".equals(method)) return show(rdata);
    if (!sdata.isEditor())
      throw new RightException();
    if ("openEditDocuments".equals(method)) return openEditDocuments(sdata);
    if ("reopenEditDocuments".equals(method)) return reopenEditDocuments(rdata, sdata);
    if ("openFckDocumentSelector".equals(method)) return openFckDocumentSelector(sdata);
    if ("openFieldDocumentSelector".equals(method)) return openFieldDocumentSelector(sdata);
    if ("reopenSelectDocuments".equals(method)) return reopenSelectDocuments(rdata, sdata);
    if ("openDocumentUpload".equals(method)) return openDocumentUpload(sdata);
    if ("openDocumentUploadSelect".equals(method)) return openDocumentUploadSelect(sdata);
    if ("uploadDocument".equals(method)) return uploadDocument(rdata, sdata);
    if ("uploadDocumentSelect".equals(method)) return uploadDocumentSelect(rdata, sdata);
    if ("openDocumentUpdate".equals(method)) return openDocumentUpdate(rdata, sdata);
    if ("updateDocument".equals(method)) return updateDocument(rdata, sdata);
    if ("openDeleteDocument".equals(method)) return openDeleteDocument(rdata, sdata);
    if ("deleteDocument".equals(method)) return deleteDocument(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method openEditDocuments
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditDocuments(SessionData sdata) throws Exception {
    DocumentSelectData data = new DocumentSelectData();
    data.setDocuments(getDocumentBean().getDocumentList());
    sdata.setParam("documentSelect", data);
    return new PageResponse(Strings.getString("documentEdit", sdata.getLocale()), "", documentEditAllJsp);
  }

  /**
   * Method reopenEditDocuments
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response reopenEditDocuments(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noData(rdata, sdata);
    data.setDocuments(getDocumentBean().getDocumentList());
    return new PageResponse(Strings.getString("documentEdit", sdata.getLocale()), "", documentEditAllJsp);
  }

  /**
   * Method show
   *
   * @param rdata of type RequestData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response show(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("did");
    FileData data = getDocumentBean().getDocument(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown document - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

  /**
   * Method openFckDocumentSelector
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openFckDocumentSelector(SessionData sdata) throws Exception {
    DocumentSelectData data = new DocumentSelectData();
    data.setForFck(true);
    data.setDocuments(getDocumentBean().getDocumentList());
    sdata.setParam("documentSelect", data);
    return new PopupResponse(Strings.getString("documentSelect", sdata.getLocale()), documentSelectJsp);
  }

  /**
   * Method openFieldDocumentSelector
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openFieldDocumentSelector(SessionData sdata) throws Exception {
    DocumentSelectData data = new DocumentSelectData();
    data.setForFck(false);
    data.setDocuments(getDocumentBean().getDocumentList());
    sdata.setParam("documentSelect", data);
    return new PopupResponse(Strings.getString("documentSelect", sdata.getLocale()), documentSelectJsp);
  }

  /**
   * Method reopenSelectDocuments
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response reopenSelectDocuments(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    data.setDocuments(getDocumentBean().getDocumentList());
    return new PopupResponse(Strings.getString("documentSelect", sdata.getLocale()), documentSelectJsp);
  }

  /**
   * Method openDocumentUpload
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDocumentUpload(SessionData sdata) throws Exception {
    DocumentData ddata = new DocumentData();
    ddata.setId(getDocumentBean().getNextId());
    ddata.setBeingCreated(true);
    sdata.setParam("document", ddata);
    return new PageResponse(Strings.getString("documentUpload", sdata.getLocale()), "", documentUploadJsp);
  }

  /**
   * Method openDocumentUploadSelect
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDocumentUploadSelect(SessionData sdata) throws Exception {
    DocumentData ddata = new DocumentData();
    ddata.setId(getDocumentBean().getNextId());
    ddata.setBeingCreated(true);
    sdata.setParam("document", ddata);
    return new PopupResponse(Strings.getString("documentUpload", sdata.getLocale()), documentUploadSelectJsp);
  }

  /**
   * Method uploadDocument
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response uploadDocument(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    DocumentData ddata = (DocumentData) sdata.getParam("document");
    if (ddata == null)
      return openDocumentUpload(sdata);
    if (!ddata.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("documentUpload", sdata.getLocale()), "", documentUploadJsp);
    getDocumentBean().saveDocumentData(ddata);
    if (data.getDocuments() != null)
      data.getDocuments().add(0, ddata);
    return new PageResponse(Strings.getString("documentUploadOk", sdata.getLocale()), "", documentUploadOkJsp);
  }

  /**
   * Method uploadDocumentSelect
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response uploadDocumentSelect(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    DocumentData ddata = (DocumentData) sdata.getParam("document");
    if (ddata == null)
      return openDocumentUpload(sdata);
    if (!ddata.readRequestData(rdata, sdata))
      return new PopupResponse(Strings.getString("documentUpload", sdata.getLocale()), documentUploadSelectJsp);
    getDocumentBean().saveDocumentData(ddata);
    if (data.getDocuments() != null)
      data.getDocuments().add(0, ddata);
    return new PopupResponse(Strings.getString("documentUploadOk", sdata.getLocale()), documentUploadSelectOkJsp);
  }

  /**
   * Method openDocumentUpdate
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDocumentUpdate(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null) {
      data = new DocumentSelectData();
      sdata.setParam("documentSelect", data);
    }
    int id = rdata.getParamInt("id", -1);
    DocumentData idata = getDocumentBean().getDocumentData(id);
    sdata.setParam("document", idata);
    return new PageResponse(Strings.getString("documentUpdate", sdata.getLocale()), "", documentUpdateJsp);
  }

  /**
   * Method updateDocument
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response updateDocument(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noData(rdata, sdata);
    DocumentData ddata = (DocumentData) sdata.getParam("document");
    if (ddata == null)
      return openDocumentUpdate(rdata, sdata);
    if (!ddata.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("documentUpdate", sdata.getLocale()), "", documentUpdateJsp);
    getDocumentBean().saveDocumentData(ddata);
    if (data.getDocuments() != null) {
      for (int i = 0; i < data.getDocuments().size(); i++) {
        DocumentData doc = data.getDocuments().get(i);
        if (doc.getId() == ddata.getId()) {
          ddata.copyMetaData(doc);
          break;
        }
      }
    }
    return openEditDocuments(sdata);
  }

  /**
   * Method openDeleteDocument
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDeleteDocument(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("did");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return openEditDocuments(sdata);
    }
    if (getDocumentBean().isDocumentInUse(id)) {
      addError(rdata, Strings.getString("err_document_in_use", sdata.getLocale()));
      return openEditDocuments(sdata);
    }
    return new PageResponse(Strings.getString("documentDelete", sdata.getLocale()), "", documentDeleteJsp);
  }

  /**
   * Method deleteDocument
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response deleteDocument(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("did", -1);
    getDocumentBean().deleteDocument(id);
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    DocumentData idata;
    if (data != null && data.getDocuments() != null) {
      for (int i = 0; i < data.getDocuments().size(); i++) {
        idata = data.getDocuments().get(i);
        if (idata.getId() == id) {
          data.getDocuments().remove(i);
          break;
        }
      }
    }
    return openEditDocuments(sdata);
  }

}
