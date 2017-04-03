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
import de.bandika.data.*;
import de.bandika.response.Response;
import de.bandika.response.MsgResponse;
import de.bandika.response.JspResponse;
import de.bandika.page.fields.*;
import de.bandika.user.UserController;
import de.bandika.user.AppUserBean;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PageController extends Controller {

	private static PageController instance=null;

  public static PageController getInstance(){
    if (instance==null)
      instance=new PageController();
    return instance;
  }

	public static final int PAGE_MIN = 1000;

	public static final int EDIT_CONTENT = 1;
	public static final int EDIT_METADATA = 2;
  public static final int EDIT_PARENT = 3;
  public static final int EDIT_RIGHTS = 4;

	protected String getEditJsp(RequestData rdata) {
    int editType = rdata.getParamInt("editType");
    switch (editType) {
      case EDIT_CONTENT:
        return "/_jsp/pageEditContent.jsp";
      case EDIT_METADATA:
        return "/_jsp/pageEditMetaData.jsp";
			case EDIT_PARENT:
        return "/_jsp/pageEditParent.jsp";
      case EDIT_RIGHTS:
        return "/_jsp/pageEditRights.jsp";
    }
    return null;
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
      id = RequestData.ROOT_PAGE_ID;
			rdata.setCurrentPageId(id);
    }
		return id;
	}

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    int id=rdata.getParamInt("id",RequestData.ROOT_PAGE_ID);
    if (method.equals("show") || method.length() == 0) return show(rdata, sdata);
    if (!sdata.hasRight(id, RightData.RIGHT_EDIT)){
      PageData data = (PageData) sdata.getParam("pageData");
      if (data==null || !data.isBeingCreated() || !sdata.hasRight(data.getParentId(), RightData.RIGHT_EDIT))
        throw new RightException();
    }
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
    if (method.equals("switchRights")) return switchRights(rdata, sdata);
    if (method.equals("save")) return save(rdata, sdata);
		if (method.equals("openSortChildren")) return openSortChildren(rdata, sdata);
    if (method.equals("changeRanking")) return changeRanking(rdata, sdata);
    if (method.equals("saveSortChildren")) return saveSortChildren(rdata, sdata);
    if (method.equals("openDelete")) return openDelete(rdata, sdata);
    if (method.equals("delete")) return delete(rdata, sdata);
    return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
  }

  public Response showHome(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setCurrentJsp("");
    rdata.setParam("method","show");
    rdata.setCurrentPageId(RequestData.ROOT_PAGE_ID);
    return show(rdata,sdata);
  }

  public Response show(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = PageBean.getInstance().getPage(id);
    if (data == null) {
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("notcomplete"));
    }
    if (data.isRestricted() && !sdata.hasRight(id,RightData.RIGHT_READ)){
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
    }
    rdata.setParam("pageData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageShow.jsp");
  }

  public Response openCreate(RequestData rdata, SessionData sdata) throws Exception {
    int parent = getPageId(rdata);
    PageBean ts = PageBean.getInstance();
    PageData parentData=MenuController.getInstance().getPage(parent);
    PageData data = getNewPageData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.setParentId(parent);
    data.setGroupRights(new HashMap<Integer,Integer>(parentData.getGroupRights()));
    data.setRanking(parentData.childPages.size());
    data.prepareEditing();
    sdata.setParam("pageData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditMetaData.jsp");
  }

  public Response openEdit(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPage(id);
    if (data == null) {
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("notcomplete"));
    }
    data.prepareEditing();
    sdata.setParam("pageData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response addParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    int idx = rdata.getParamInt("idx", -1);
    String tname = rdata.getParamString("template");
    ParagraphData pdata = new ParagraphData();
    pdata.setId(PageBean.getInstance().getNextId());
    pdata.setPageId(data.getId());
    pdata.setTemplateName(tname);
    pdata.setBeingCreated(true);
    if (idx == -1) {
      data.getParagraphs().add(pdata);
      idx = data.getParagraphs().size() - 1;
    } else
      data.getParagraphs().add(idx, pdata);
    data.setEditParagraph(idx);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response editParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    int idx = rdata.getParamInt("idx");
    data.setEditParagraph(idx);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response moveParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    int idx = rdata.getParamInt("idx");
    int dir = rdata.getParamInt("dir");
    data.moveParagraph(idx, dir);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response cancelEditParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    ParagraphData pdata = data.getEditParagraph();
    if (pdata != null && pdata.getTemplateName().equals(""))
      data.getParagraphs().remove(pdata);
    data.setEditParagraph(null);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response saveParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    int idx = rdata.getParamInt("idx");
    ParagraphData pdata = data.getEditParagraph();
    if (pdata == null || data.getParagraphs().get(idx) != pdata) {
      return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
    }
    if (!readParagraphRequestData(pdata,rdata)) {
      return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
    }
    data.setEditParagraph(null);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  protected boolean readParagraphRequestData(ParagraphData data,RequestData rdata){
    boolean complete=true;
    for (BaseField field : data.getFields().values()) {
      int fieldType=field.getFieldType();
      switch (fieldType){
        case BaseField.FIELDTYPE_TEXTLINE: {
          TextLineField fld=(TextLineField)field;
          fld.setText(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_TEXTAREA: {
          TextAreaField fld=(TextAreaField)field;
          fld.setText(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_HTML: {
          HtmlField fld=(HtmlField)field;
          fld.setHtml(rdata.getParamString(fld.getIdentifier()));
        }
        break;
        case BaseField.FIELDTYPE_IMAGE: {
          ImageField fld=(ImageField)field;
          String ident=fld.getIdentifier();
          fld.setImgId(rdata.getParamInt(ident + "ImgId"));
          fld.setWidth(rdata.getParamInt(ident + "Width"));
          fld.setHeight(rdata.getParamInt(ident + "Height"));
          fld.setAltText(rdata.getParamString(ident + "Alt"));
        }
        break;
        case BaseField.FIELDTYPE_DOCUMENT: {
          DocumentField fld=(DocumentField)field;
          String ident=fld.getIdentifier();
          fld.setDocId(rdata.getParamInt(ident + "DocId"));
          fld.setText(rdata.getParamString(ident + "Text"));
        }
        break;
        case BaseField.FIELDTYPE_BLOG: {
          //BlogField fld=(BlogField)field;
        }
        break;
        case BaseField.FIELDTYPE_MAIL: {
          MailField fld=(MailField)field;
          fld.setReceiver(rdata.getParamString(fld.getIdentifier()));
        }
        break;
      }
      complete &= field.isComplete();
    }
    return complete;
  }

  public Response deleteParagraph(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    int idx = rdata.getParamInt("idx");
    data.removeParagraph(idx);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  protected PageData assertSessionData(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id) {
      data = PageBean.getInstance().getPage(rdata.getCurrentPageId());
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
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readPageRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp",getEditJsp(rdata));
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditContent.jsp");
  }

  public Response switchMetaData(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readPageRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp",getEditJsp(rdata));
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditMetaData.jsp");
  }

	public Response switchParent(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readPageRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp",getEditJsp(rdata));
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditParent.jsp");
  }

  public Response switchRights(RequestData rdata, SessionData sdata) throws Exception {
		PageData data = assertSessionData(rdata, sdata);
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readPageRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp",getEditJsp(rdata));
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageEditRights.jsp");
  }

  public Response save(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId()!=id)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readPageRequestData(data, rdata) || !data.isAllComplete(rdata))
      return new JspResponse("/_jsp/master.jsp",getEditJsp(rdata));
    data.setAuthorName(sdata.getUserName());
    data.prepareSave();
    PageBean ts = PageBean.getInstance();
    ts.savePage(data);
    MenuController.getInstance().setDirty();
    return show(rdata, sdata);
  }

  public boolean readPageRequestData(PageData data, RequestData rdata) {
		int editType = rdata.getParamInt("editType");
    RequestError err=new RequestError();
		switch (editType) {
			case PageController.EDIT_METADATA: {
				data.setName(rdata.getParamString("name"));
				data.setDescription(rdata.getParamString("description"));
				data.setKeywords(rdata.getParamString("metaKeywords"));
				data.setRestricted(rdata.getParamBoolean("restricted"));
				HashSet<Integer> groupIds = rdata.getParamIntegerSet("groupIds");
				data.setGroupRights(groupIds, UserController.RIGHT_READ);
				if (!DataHelper.isComplete(data.name))
					err.addErrorString(Strings.getHtml("notcomplete"));
			}
			break;
			case PageController.EDIT_PARENT: {
        int par = rdata.getParamInt("parent");
        if (MenuController.getInstance().isParentPage(data.getId(), par))
          err.addErrorString(Strings.getHtml("badparent"));
        else
          data.setParentId(par);
        if (!DataHelper.isComplete(par) && data.getId() != RequestData.ROOT_PAGE_ID)
          err.addErrorString(Strings.getHtml("notcomplete"));
			}
			break;
      case PageController.EDIT_RIGHTS: {
        data.getGroupRights().clear();
        ArrayList<Integer> appGroupIds= AppUserBean.getInstance().getAllAppGroupIds();
        for (Integer gid : appGroupIds){
          HashSet<Integer> groupRights = rdata.getParamIntegerSet("groupright_"+gid);
          int right=0;
          for (Integer rgt : groupRights)
            right+=rgt;
          if (right>0)
            data.getGroupRights().put(gid,right);
        }
			}
			break;
			case PageController.EDIT_CONTENT: {
        if (data.getEditParagraph() != null)
          readParagraphRequestData(data.getEditParagraph(),rdata);
			}
			break;
			default:
				err.addErrorString(Strings.getHtml("notcomplete"));
		}
		if (!err.isEmpty()) {
			rdata.setError(err);
			return false;
		}
		return true;
	}

	public Response openSortChildren(RequestData rdata, SessionData sdata) throws Exception {
		int id=getPageId(rdata);
    PageBean ts = PageBean.getInstance();
    PageSortData sortData = ts.getSortData(id);
    if (sortData.getChildren().size() <= 1) {
      addError(rdata, Strings.getHtml("nothingtosort"));
      return show(rdata, sdata);
    }
    sdata.setParam("sortData", sortData);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageSortChildren.jsp");
  }

  public Response changeRanking(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, Strings.getHtml("nodata"));
      return show(rdata, sdata);
    }
    readSortRequestData(sortData, rdata);
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageSortChildren.jsp");
  }

	public boolean readSortRequestData(PageSortData sortData, RequestData rdata) {
		int idx = rdata.getParamInt("childIdx");
		int childRanking = rdata.getParamInt("childRanking");
		PageSortData child = sortData.getChildren().remove(idx);
		if (childRanking >= sortData.getChildren().size())
			sortData.getChildren().add(child);
		else
			sortData.getChildren().add(childRanking, child);
		for (int i = 0; i < sortData.getChildren().size(); i++)
			sortData.getChildren().get(i).setRanking(i);
		return true;
	}

  public Response saveSortChildren(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, Strings.getHtml("nodata"));
      return show(rdata, sdata);
    }
    PageBean ts = PageBean.getInstance();
    ts.saveSortData(sortData);
    MenuController.getInstance().setDirty();
    return show(rdata, sdata);
  }

  public Response openDelete(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    if (id == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return show(rdata, sdata);
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/pageDelete.jsp");
  }

  public Response delete(RequestData rdata, SessionData sdata) throws Exception {
    int id=getPageId(rdata);
    if (id < PAGE_MIN) {
      addError(rdata, Strings.getHtml("notdeletable"));
      return show(rdata, sdata);
    }
    int parent = MenuController.getInstance().getParentPage(id);
    PageBean ts = PageBean.getInstance();
    ts.deletePage(id);
    MenuController.getInstance().setDirty();
    rdata.setCurrentPageId(parent);
    return show(rdata, sdata);
  }

}
