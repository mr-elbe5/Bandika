/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.base.controller.*;
import de.net25.base.exception.RightException;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

/**
 * Class ContentController is the controller class for content pages. <br>
 * Usage:
 */
public class ContentController extends Controller {

  public String showJsp = "/jsps/content/show.jsp";
  public String editContentJsp = "/jsps/content/editContent.jsp";
  public String editParentJsp = "/jsps/content/editParent.jsp";
  public String editMetaDataJsp = "/jsps/content/editMetaData.jsp";
  public String sortChildrenJsp = "/jsps/content/sortChildren.jsp";
  public String deleteJsp = "/jsps/content/delete.jsp";

  /**
   * Method getEditJsp
   *
   * @param rdata of type RequestData
   * @return String
   */
  protected String getEditJsp(RequestData rdata) {
    int editType = rdata.getParamInt("editType");
    switch (editType) {
      case ContentData.EDIT_CONTENT:
        return editContentJsp;
      case ContentData.EDIT_METADATA:
        return editMetaDataJsp;
      case ContentData.EDIT_PARENT:
        return editParentJsp;
    }
    return null;
  }

  /**
   * Method getBean returns the bean of this ContentController object.
   *
   * @return the bean (type ContentBean) of this ContentController object.
   */
  public ContentBean getBean() {
    return (ContentBean) Statics.getBean(Statics.KEY_CONTENT);
  }

  /**
   * Method getNewContentData returns the newContentData of this ContentController object.
   *
   * @return the newContentData (type ContentData) of this ContentController object.
   */
  protected ContentData getNewContentData() {
    return new ContentData();
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
    if (method.equals("show") || method.length() == 0) return show(rdata, sdata);
    if (!sdata.isEditor())
      throw new RightException();
    if (method.equals("openCreate")) return openCreate(rdata, sdata);
    if (method.equals("openEdit")) return openEdit(rdata, sdata);
    if (method.equals("addParagraph")) return addParagraph(rdata, sdata);
    if (method.equals("setTemplate")) return setTemplate(rdata, sdata);
    if (method.equals("editParagraph")) return editParagraph(rdata, sdata);
    if (method.equals("moveParagraph")) return moveParagraph(rdata, sdata);
    if (method.equals("deleteParagraph")) return deleteParagraph(rdata, sdata);
    if (method.equals("cancelEditParagraph")) return cancelEditParagraph(rdata, sdata);
    if (method.equals("saveParagraph")) return saveParagraph(rdata, sdata);
    if (method.equals("switchContent")) return switchContent(rdata, sdata);
    if (method.equals("switchMetaData")) return switchMetaData(rdata, sdata);
    if (method.equals("switchParent")) return switchParents(rdata, sdata);
    if (method.equals("save")) return save(rdata, sdata);
    if (method.equals("openSortChildren")) return openSortChildren(rdata, sdata);
    if (method.equals("changeRanking")) return changeRanking(rdata, sdata);
    if (method.equals("saveSortChildren")) return saveSortChildren(rdata, sdata);
    if (method.equals("openDelete")) return openDelete(rdata, sdata);
    if (method.equals("delete")) return delete(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method show
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response show(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("id");
    if (id == 0) {
      id = Statics.getContentHomeId(sdata.getLocale());
      rdata.setParam("id", Integer.toString(Statics.getContentHomeId(sdata.getLocale())));
    }
    ContentData data = getBean().getContentFromCache(id);
    if (data == null) {
      return showError(rdata, "err_not_complete");
    }
    rdata.setParam("contentData", data);
    return new PageResponse(data.getName(), data.getMetaKeywords(), showJsp);
  }

  /**
   * Method openCreate
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openCreate(RequestData rdata, SessionData sdata) throws Exception {
    int parent = rdata.getParamInt("parent");
    ContentBean ts = getBean();
    ContentData data = getNewContentData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.setParent(parent, sdata.getLocale());
    data.prepareEditing();
    sdata.setParam("contentData", data);
    return new PageResponse(Strings.getString("newPage", sdata.getLocale()), "", editMetaDataJsp);
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
    int id = rdata.getParamInt("id");
    ContentBean ts = getBean();
    ContentData data = ts.getContent(id);
    if (data == null) {
      return showError(rdata, "err_not_complete");
    }
    data.prepareEditing();
    sdata.setParam("contentData", data);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method addParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response addParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx", -1);
    ParagraphData pdata = new ParagraphData();
    pdata.setId(getBean().getNextId());
    pdata.setContentId(data.getId());
    pdata.setTemplateId(0);
    pdata.setBeingCreated(true);
    if (idx == -1) {
      data.getParagraphs().add(pdata);
      idx = data.getParagraphs().size() - 1;
    } else
      data.getParagraphs().add(idx, pdata);
    data.setEditParagraph(idx);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method setTemplate
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response setTemplate(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    ParagraphData pdata = data.getEditParagraph();
    if (pdata == null)
      return noData(rdata, sdata);
    int tid = rdata.getParamInt("template");
    pdata.setTemplateId(tid);
    pdata.setBeingCreated(true);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method editParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response editParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    data.setEditParagraph(idx);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method moveParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response moveParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    int dir = rdata.getParamInt("dir");
    data.moveParagraph(idx, dir);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method cancelEditParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response cancelEditParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    ParagraphData pdata = data.getEditParagraph();
    if (pdata != null && pdata.getTemplateId() == 0)
      data.getParagraphs().remove(pdata);
    data.setEditParagraph(null);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method saveParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response saveParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    ParagraphData pdata = data.getEditParagraph();
    if (pdata == null || data.getParagraphs().get(idx) != pdata) {
      return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
    }
    if (!pdata.readRequestData(rdata, sdata)) {
      return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
    }
    data.setEditParagraph(null);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method deleteParagraph
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response deleteParagraph(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    data.removeParagraph(idx);
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method assertSessionData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return ContentData
   * @throws Exception when data processing is not successful
   */
  protected ContentData assertSessionData(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = (ContentData) sdata.getParam("contentData");
    int id = rdata.getParamInt("id");
    if (data == null || id != data.getId()) {
      data = getBean().getContent(id);
      if (data == null)
        return null;
      data.prepareEditing();
      sdata.setParam("contentData", data);
    }
    return data;
  }

  /**
   * Method switchContent
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response switchContent(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new PageResponse(getEditJsp(rdata));
    return new PageResponse(data.getName(), data.getMetaKeywords(), editContentJsp);
  }

  /**
   * Method switchMetaData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response switchMetaData(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new PageResponse(getEditJsp(rdata));
    return new PageResponse(data.getName(), data.getMetaKeywords(), editMetaDataJsp);
  }

  /**
   * Method switchParents
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response switchParents(RequestData rdata, SessionData sdata) throws Exception {
    ContentData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new PageResponse(getEditJsp(rdata));
    return new PageResponse(data.getName(), data.getMetaKeywords(), editParentJsp);
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
    ContentData data = (ContentData) sdata.getParam("contentData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata) || !data.isAllComplete(rdata, sdata))
      return new PageResponse(data.getName(), data.getMetaKeywords(), getEditJsp(rdata));
    data.setAuthorId(sdata.getUserId());
    data.prepareSave();
    ContentBean ts = getBean();
    ts.saveContent(data);
    MenuCache.getMenuCache(sdata.getLocale()).setDirty();
    rdata.setParam("id", Integer.toString(data.getId()));
    return show(rdata, sdata);
  }

  /**
   * Method openSortChildren
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openSortChildren(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("id");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return show(rdata, sdata);
    }
    ContentBean ts = getBean();
    SortData sortData = ts.getSortData(id);
    if (sortData.getChildren().size() <= 1) {
      addError(rdata, Strings.getString("err_nothing_to_sort", sdata.getLocale()));
      return show(rdata, sdata);
    }
    sdata.setParam("sortData", sortData);
    return new PageResponse(sortData.getName(), "", sortChildrenJsp);
  }

  /**
   * Method reopenSortChildren
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response changeRanking(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("id");
    SortData sortData = (SortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, Strings.getString("err_no_data", sdata.getLocale()));
      return show(rdata, sdata);
    }
    sortData.readRequestData(rdata, sdata);
    return new PageResponse(sortData.getName(), "", sortChildrenJsp);
  }

  /**
   * Method saveSortChildren
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response saveSortChildren(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("id");
    SortData sortData = (SortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, Strings.getString("err_no_data", sdata.getLocale()));
      return show(rdata, sdata);
    }
    ContentBean ts = getBean();
    ts.saveSortData(sortData);
    MenuCache.getMenuCache(sdata.getLocale()).setDirty();
    return show(rdata, sdata);
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
    int id = rdata.getParamInt("id");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return show(rdata, sdata);
    }
    return new PageResponse(Strings.getString("deleteContent", sdata.getLocale()), "", deleteJsp);
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
    int id = rdata.getParamInt("id");
    if (id < Statics.CONT_MIN) {
      addError(rdata, Strings.getString("err_not_deletable", sdata.getLocale()));
      return show(rdata, sdata);
    }
    int parent = MenuCache.getInstance(sdata.getLocale()).getParent(id);
    ContentBean ts = getBean();
    ts.deleteContent(id);
    MenuCache.getMenuCache(sdata.getLocale()).setDirty();
    rdata.setParam("id", Integer.toString(parent));
    return show(rdata, sdata);
  }

}
