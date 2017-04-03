/*
  Bandika! - A Java based Page Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.*;
import de.bandika.menu.MenuController;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;
import de.bandika.http.Response;
import de.bandika.http.JspResponse;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PageController extends Controller {

	public static String KEY_PAGE = "page";

	public static final int PAGE_MIN = 1000;

	public static final int EDIT_CONTENT = 1;
	public static final int EDIT_METADATA = 2;
  public static final int EDIT_PARENT = 3;

	public PageBean getPageBean() {
    return (PageBean) Bean.getBean(KEY_PAGE);
  }

  public MenuController getMenuController() {
    return (MenuController) Controller.getController(MenuController.KEY_MENU);
  }

	protected String getEditJsp(RequestData rdata) {
    int editType = rdata.getParamInt("editType");
    switch (editType) {
      case EDIT_CONTENT:
        return "/_jsp/pageEditContent.jsp";
      case EDIT_METADATA:
        return "/_jsp/pageEditMetaData.jsp";
			case EDIT_PARENT:
        return "/_jsp/pageEditParent.jsp";
    }
    return null;
  }

  public PageBean getBean() {
    return (PageBean) Bean.getBean(KEY_PAGE);
  }

  protected PageData getNewPageData() {
    return new PageData();
  }

	protected int getPageId(RequestData rdata){
		int id=rdata.getParamInt("id");
		if (id!=0)
		  rdata.setCurrentPageId(id);
    else
			id = rdata.getCurrentPageId();
    if (id == 0) {
      id = BaseConfig.ROOT_PAGE_ID;
			rdata.setCurrentPageId(id);
    }
		return id;
	}

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (method.equals("show") || method.length() == 0) return show(rdata, sdata);
    if (!sdata.isEditor())
      throw new RightException();
		if (method.equals("openCreate")) return openCreate(rdata, sdata);
    if (method.equals("openEdit")) return openEdit(rdata, sdata);
    if (method.equals("addParagraph")) return addParagraph(rdata, sdata);
    if (method.equals("editParagraph")) return editParagraph(rdata, sdata);
    if (method.equals("moveParagraph")) return moveParagraph(rdata, sdata);
    if (method.equals("deleteParagraph")) return deleteParagraph(rdata, sdata);
    if (method.equals("cancelEditParagraph")) return cancelEditParagraph(rdata, sdata);
    if (method.equals("saveParagraph")) return saveParagraph(rdata, sdata);
    if (method.equals("switchContent")) return switchContent(rdata, sdata);
    if (method.equals("switchMetaData")) return switchMetaData(rdata, sdata);
		if (method.equals("switchParent")) return switchParent(rdata, sdata);
    if (method.equals("save")) return save(rdata, sdata);
		if (method.equals("openSortChildren")) return openSortChildren(rdata, sdata);
    if (method.equals("changeRanking")) return changeRanking(rdata, sdata);
    if (method.equals("saveSortChildren")) return saveSortChildren(rdata, sdata);
    if (method.equals("openDelete")) return openDelete(rdata, sdata);
    if (method.equals("delete")) return delete(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  public Response showHome(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setCurrentJsp("");
    rdata.setParam("ctrl","page");
    rdata.setParam("method","show");
    rdata.setCurrentPageId(BaseConfig.ROOT_PAGE_ID);
    return show(rdata,sdata);
  }

  public Response show(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = getBean().getPage(id);
    if (data == null) {
      return showError(rdata, "err_not_complete");
    }
    if (data.isRestricted() && !sdata.isEditor() && !sdata.hasUserReadRight(id)){
      return this.noMethod(rdata,sdata);
    }
    rdata.setParam("pageData", data);
    return new JspResponse("/index.jsp");
  }

  public Response openCreate(RequestData rdata, SessionData sdata) throws Exception {
    int parent = rdata.getParamInt("parent");
    PageBean ts = getBean();
    PageData parentData=getMenuController().getPage(parent);
    PageData data = getNewPageData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.setParentId(parent);
    data.setRanking(parentData.childPages.size());
    data.prepareEditing();
    sdata.setParam("pageData", data);
    return new JspResponse("/_jsp/pageEditMetaData.jsp");
  }

  public Response openEdit(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageBean ts = getBean();
    PageData data = ts.getPage(id);
    if (data == null) {
      return showError(rdata, "err_not_complete");
    }
    data.prepareEditing();
    sdata.setParam("pageData", data);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response addParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx", -1);
    String tname = rdata.getParamString("template");
    ParagraphData pdata = new ParagraphData();
    pdata.setId(getBean().getNextId());
    pdata.setPageId(data.getId());
    pdata.setTemplateName(tname);
    pdata.setBeingCreated(true);
    if (idx == -1) {
      data.getParagraphs().add(pdata);
      idx = data.getParagraphs().size() - 1;
    } else
      data.getParagraphs().add(idx, pdata);
    data.setEditParagraph(idx);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response editParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    data.setEditParagraph(idx);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response moveParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    int dir = rdata.getParamInt("dir");
    data.moveParagraph(idx, dir);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response cancelEditParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    ParagraphData pdata = data.getEditParagraph();
    if (pdata != null && pdata.getTemplateName().equals(""))
      data.getParagraphs().remove(pdata);
    data.setEditParagraph(null);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response saveParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    ParagraphData pdata = data.getEditParagraph();
    if (pdata == null || data.getParagraphs().get(idx) != pdata) {
      return new JspResponse("/_jsp/pageEditContent.jsp");
    }
    if (!pdata.readRequestData(rdata, sdata)) {
      return new JspResponse("/_jsp/pageEditContent.jsp");
    }
    data.setEditParagraph(null);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response deleteParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    int idx = rdata.getParamInt("idx");
    data.removeParagraph(idx);
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  protected PageData assertSessionData(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id) {
      data = getBean().getPage(rdata.getCurrentPageId());
      if (data == null)
        return null;
      data.prepareEditing();
      sdata.setParam("pageData", data);
    }
    return data;
  }

  public Response switchContent(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new JspResponse(getEditJsp(rdata));
    return new JspResponse("/_jsp/pageEditContent.jsp");
  }

  public Response switchMetaData(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new JspResponse(getEditJsp(rdata));
    return new JspResponse("/_jsp/pageEditMetaData.jsp");
  }

	public Response switchParent(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new JspResponse(getEditJsp(rdata));
    return new JspResponse("/_jsp/pageEditParent.jsp");
  }

  public Response save(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata) || !data.isAllComplete(rdata, sdata))
      return new JspResponse(getEditJsp(rdata));
    data.setAuthorId(sdata.getUserId());
    data.prepareSave();
    PageBean ts = getBean();
    ts.savePage(data);
    getMenuController().setDirty();
    return show(rdata, sdata);
  }

	public Response openSortChildren(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageBean ts = getBean();
    PageSortData sortData = ts.getSortData(id);
    if (sortData.getChildren().size() <= 1) {
      addError(rdata, AdminStrings.nothingtosort);
      return show(rdata, sdata);
    }
    sdata.setParam("sortData", sortData);
    return new JspResponse("/_jsp/pageSortChildren.jsp");
  }

  public Response changeRanking(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, AdminStrings.nodata);
      return show(rdata, sdata);
    }
    sortData.readRequestData(rdata, sdata);
    return new JspResponse("/_jsp/pageSortChildren.jsp");
  }

  public Response saveSortChildren(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, AdminStrings.nodata);
      return show(rdata, sdata);
    }
    PageBean ts = getBean();
    ts.saveSortData(sortData);
    getMenuController().setDirty();
    return show(rdata, sdata);
  }

  public Response openDelete(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    if (id == 0) {
      addError(rdata, AdminStrings.noselection);
      return show(rdata, sdata);
    }
    return new JspResponse("/_jsp/pageDelete.jsp");
  }

  public Response delete(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    if (id < PAGE_MIN) {
      addError(rdata, AdminStrings.notdeletable);
      return show(rdata, sdata);
    }
    int parent = getMenuController().getParentPage(id);
    PageBean ts = getBean();
    ts.deletePage(id);
    getMenuController().setDirty();
    rdata.setCurrentPageId(parent);
    return show(rdata, sdata);
  }

}
