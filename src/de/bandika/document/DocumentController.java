/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.document;

import de.bandika.base.*;
import de.bandika.http.*;

import java.util.ArrayList;

/**
 * Class DocumentController is the controller class for document files. <br>
 * Usage:
 */
public class DocumentController extends Controller {

	public static String KEY_DOCUMENT = "doc";

	public DocumentBean getDocumentBean() {
    return (DocumentBean) Bean.getBean(KEY_DOCUMENT);
  }

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if ("show".equals(method)) return show(rdata);
		if (!sdata.isEditor())
      throw new RightException();
    if ("openEditDocuments".equals(method)) return openEditDocuments(sdata);
    if ("previousEditPage".equals(method)) return previousEditPage(rdata, sdata);
    if ("nextEditPage".equals(method)) return nextEditPage(rdata, sdata);
    if ("toEditPage".equals(method)) return toEditPage(rdata, sdata);
    if ("openDocumentSelector".equals(method)) return openDocumentSelector(rdata,sdata);
    if ("previousSelectPage".equals(method)) return previousSelectPage(rdata, sdata);
    if ("nextSelectPage".equals(method)) return nextSelectPage(rdata, sdata);
    if ("toSelectPage".equals(method)) return toSelectPage(rdata, sdata);
    if ("openDocumentUpload".equals(method)) return openDocumentUpload(sdata);
    if ("uploadDocument".equals(method)) return uploadDocument(rdata, sdata);
    if ("openChangeDocument".equals(method)) return openChangeDocument(rdata, sdata);
    if ("updateDocument".equals(method)) return updateDocument(rdata, sdata);
    if ("openDeleteDocument".equals(method)) return openDeleteDocument(rdata, sdata);
    if ("deleteDocument".equals(method)) return deleteDocument(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  public Response openEditDocuments(SessionData sdata) throws Exception {
    DocumentSelectData data = new DocumentSelectData();
    data.setDocuments(getDocumentBean().getDocumentList(true));
    sdata.setParam("documentSelect", data);
    return new JspResponse("/_jsp/documentEditAll.jsp");
  }

  public Response previousEditPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    data.setPreviousPage();
    return new JspResponse("/_jsp/documentEditAll.jsp");
  }

  public Response nextEditPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    data.setNextPage();
    return new JspResponse("/_jsp/documentEditAll.jsp");
  }

  public Response toEditPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    int page=rdata.getParamInt("page");
    data.setPage(page);
    return new JspResponse("/_jsp/documentEditAll.jsp");
  }

  public Response openDocumentSelector(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = new DocumentSelectData();
    data.setForHtmlEditor(rdata.getParamBoolean("forCk"));
    data.setDocuments(getDocumentBean().getDocumentList(false));
    sdata.setParam("documentSelect", data);
    return new JspResponse("/_jsp/documentSelect.jsp");
  }

  public Response previousSelectPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    data.setPreviousPage();
    return new JspResponse("/_jsp/documentSelect.jsp");
  }

  public Response nextSelectPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    data.setNextPage();
    return new JspResponse("/_jsp/documentSelect.jsp");
  }

  public Response toSelectPage(RequestData rdata,SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    int page=rdata.getParamInt("page");
    data.setPage(page);
    return new JspResponse("/_jsp/documentSelect.jsp");
  }

  public Response openDocumentUpload(SessionData sdata) throws Exception {
    DocumentData ddata = new DocumentData();
    ddata.setId(getDocumentBean().getNextId());
    ddata.setBeingCreated(true);
    sdata.setParam("document", ddata);
    return new JspResponse("/_jsp/documentUpload.jsp");
  }

  public Response uploadDocument(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    boolean popup=rdata.getParamBoolean("popup");
    if (data == null)
      return noData(rdata, sdata);
    DocumentData ddata = (DocumentData) sdata.getParam("document");
    if (ddata == null)
      return openDocumentUpload(sdata);
    if (!ddata.readRequestData(rdata, sdata))
      return new JspResponse("/_jsp/documentUpload.jsp");
    getDocumentBean().saveDocumentData(ddata);
    if (data.getDocuments() != null)
      data.getDocuments().add(0, ddata);
    if (popup)
      return new JspResponse("/_jsp/documentSelect.jsp");
    else
      return new JspResponse("/_jsp/documentEditAll.jsp");
  }

  public Response openChangeDocument(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null) {
      data = new DocumentSelectData();
      sdata.setParam("documentSelect", data);
    }
    ArrayList<Integer> ids = rdata.getParamIntegerList("did");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditDocuments(sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, AdminStrings.singleselection);
      return openEditDocuments(sdata);
    }
    DocumentData idata = getDocumentBean().getDocumentData(ids.get(0));
    sdata.setParam("document", idata);
    return new JspResponse("/_jsp/documentUpdate.jsp");
  }

  public Response updateDocument(RequestData rdata, SessionData sdata) throws Exception {
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    if (data == null)
      return noData(rdata, sdata);
    DocumentData ddata = (DocumentData) sdata.getParam("document");
    if (ddata == null)
      return openChangeDocument(rdata, sdata);
    if (!ddata.readRequestData(rdata, sdata))
      return new JspResponse("/_jsp/documentUpdate.jsp");
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

  public Response openDeleteDocument(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("did");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditDocuments(sdata);
    }
    return new JspResponse("/_jsp/documentDelete.jsp");
  }

  public Response deleteDocument(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("did");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditDocuments(sdata);
    }
    for (int i=0;i<ids.size();i++){
      getDocumentBean().deleteDocument(ids.get(i));
    }
    DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
    DocumentData idata;
    if (data != null && data.getDocuments() != null) {
      for (int i = 0; i < data.getDocuments().size(); i++) {
        idata = data.getDocuments().get(i);
        if (ids.contains(idata.getId())) {
          data.getDocuments().remove(i);
          break;
        }
      }
    }
    return openEditDocuments(sdata);
  }

	public Response show(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("did");
    FileData data = getDocumentBean().getDocument(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown document - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

}
