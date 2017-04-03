/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.admin;

import de.bandika.base.*;
import de.bandika.document.DocumentBean;
import de.bandika.image.ImageBean;
import de.bandika.data.RequestData;
import de.bandika.data.SessionData;
import de.bandika.data.FileData;
import de.bandika.data.BaseData;
import de.bandika.response.Response;
import de.bandika.response.MsgResponse;
import de.bandika.response.JspResponse;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class AdminController extends Controller {

	private static AdminController instance=null;

  public static AdminController getInstance(){
    if (instance==null)
      instance=new AdminController();
    return instance;
  }

  @Override
  public void initialize(){
    AdminBean.getInstance().ensureTemplates();
	}

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
		if (!sdata.isAdmin())
			throw new RightException();
		if (method.equals("openConfig")) return openConfig(sdata);
		if (method.equals("openEditCaches")) return openEditCaches(sdata);
    if (method.equals("saveCaches")) return saveCaches(rdata,sdata);
    if (method.equals("replaceStylePack")) return replaceStylePack(rdata);
    if (method.equals("replaceTemplates")) return replaceTemplates(rdata);
    if (method.equals("updateTemplates")) return updateTemplates(rdata);
		return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
	}

	public Response openConfig(SessionData sdata) throws Exception {
		return new JspResponse("/_jsp/master.jsp","/_jsp/adminConfigEdit.jsp");
	}

  public Response replaceStylePack(RequestData rdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      AdminBean.getInstance().replaceStylePack(file);
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/adminConfigEdit.jsp");
  }

  public Response replaceTemplates(RequestData rdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      AdminBean.getInstance().replaceTemplates(file);
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/adminConfigEdit.jsp");
  }

  public Response updateTemplates(RequestData rdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      AdminBean.getInstance().updateTemplates(file);
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/adminConfigEdit.jsp");
  }

	public Response openEditCaches(SessionData sdata) throws Exception {
    CacheData data=new CacheData();
    data.setDocumentCacheSize(DocumentBean.getInstance().getCache().getMaxCount());
    data.setImageCacheSize(ImageBean.getInstance().getCache().getMaxCount());
    sdata.setParam("cacheData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/adminCacheEdit.jsp");
  }

  public Response saveCaches(RequestData rdata, SessionData sdata) throws Exception {
    CacheData data = (CacheData) sdata.getParam("cacheData");
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (readCacheRequestData(data,rdata)){
      DocumentBean.getInstance().getCache().setMaxCount(data.getDocumentCacheSize());
      ImageBean.getInstance().getCache().setMaxCount(data.getImageCacheSize());
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/adminCacheEdit.jsp");
  }

	public boolean readCacheRequestData(CacheData data, RequestData rdata) {
		data.setDocumentCacheSize(rdata.getParamInt("documentCacheSize"));
    data.setImageCacheSize(rdata.getParamInt("imageCacheSize"));
		return true;
	}

}