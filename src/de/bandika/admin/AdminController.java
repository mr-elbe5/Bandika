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
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;
import de.bandika.http.Response;
import de.bandika.http.JspResponse;
import de.bandika.document.DocumentController;
import de.bandika.image.ImageController;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class AdminController extends Controller {

	public static String KEY_ADMIN = "admin";

	public AdminBean getAdminBean() {
		return (AdminBean) Bean.getBean(KEY_ADMIN);
	}

  @Override
  public void initialize(){
    getAdminBean().ensureTemplates();
	}

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
		if (!sdata.isAdmin())
			throw new RightException();
		if (method.equals("openEditConfig")) return openEditConfig(sdata);
		if (method.equals("saveConfig")) return saveConfig(rdata, sdata);
		if (method.equals("openEditCaches")) return openEditCaches(sdata);
    if (method.equals("saveCaches")) return saveCaches(rdata,sdata);
    if (method.equals("replaceStylePack")) return replaceStylePack(rdata,sdata);
    if (method.equals("replaceTemplates")) return replaceTemplates(rdata,sdata);
    if (method.equals("updateTemplates")) return updateTemplates(rdata,sdata);
		return noMethod(rdata, sdata);
	}

	public Response openEditConfig(SessionData sdata) throws Exception {
    ConfigData data=new ConfigData();
    sdata.setParam("configData", data);
		return new JspResponse("/_jsp/adminConfigEdit.jsp");
	}

	public Response saveConfig(RequestData rdata, SessionData sdata) throws Exception {
    ConfigData data = (ConfigData) sdata.getParam("configData");
    if (data == null)
      return noData(rdata, sdata);
    if (data.readRequestData(rdata, sdata)){
      BaseConfig.setBasePath(data.getBasePath());
    }
		return new JspResponse("/_jsp/adminConfigEdit.jsp");
	}

  public Response replaceStylePack(RequestData rdata, SessionData sdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      getAdminBean().replaceStylePack(file);
    }
    return new JspResponse("/_jsp/adminConfigEdit.jsp");
  }

  public Response replaceTemplates(RequestData rdata, SessionData sdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      getAdminBean().replaceTemplates(file);
    }
    return new JspResponse("/_jsp/adminConfigEdit.jsp");
  }

  public Response updateTemplates(RequestData rdata, SessionData sdata) throws Exception {
    FileData file=rdata.getParamFile("zipFile");
    if (file!=null){
      getAdminBean().updateTemplates(file);
    }
    return new JspResponse("/_jsp/adminConfigEdit.jsp");
  }

	public Response openEditCaches(SessionData sdata) throws Exception {
    CacheData data=new CacheData();
    data.setDocumentCacheSize(DataCache.getCache(DocumentController.KEY_DOCUMENT).getMaxCount());
    data.setImageCacheSize(DataCache.getCache(ImageController.KEY_IMAGE).getMaxCount());
    sdata.setParam("cacheData", data);
    return new JspResponse("/_jsp/adminCacheEdit.jsp");
  }

  public Response saveCaches(RequestData rdata, SessionData sdata) throws Exception {
    CacheData data = (CacheData) sdata.getParam("cacheData");
    if (data == null)
      return noData(rdata, sdata);
    if (data.readRequestData(rdata, sdata)){
      DataCache.getCache(DocumentController.KEY_DOCUMENT).setMaxCount(data.getDocumentCacheSize());
      DataCache.getCache(ImageController.KEY_IMAGE).setMaxCount(data.getImageCacheSize());
    }
    return new JspResponse("/_jsp/adminCacheEdit.jsp");
  }

}