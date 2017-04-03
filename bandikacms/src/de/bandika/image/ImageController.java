/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.image;

import de.bandika.base.*;
import de.bandika.data.RequestData;
import de.bandika.data.SessionData;
import de.bandika.data.FileData;
import de.bandika.response.Response;
import de.bandika.response.MsgResponse;
import de.bandika.response.JspResponse;
import de.bandika.response.BinaryResponse;

import java.util.ArrayList;
import java.awt.image.BufferedImage;

/**
 * Class ImageController is the controller class for image files. <br>
 * Usage:
 */
public class ImageController extends Controller {

	private static ImageController instance=null;

	public static ImageController getInstance(){
    if (instance==null)
      instance=new ImageController();
    return instance;
  }

	public static int MAX_WIDTH = 100;
	public static int MAX_HEIGHT = 100;

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
		if ("show".equals(method)) return show(rdata);
    if ("showThumbnail".equals(method)) return showThumbnail(rdata);
    if (!sdata.isAdmin() && !sdata.hasAnyEditRight())
      throw new RightException();
    if ("openEditImages".equals(method)) return openEditImages(sdata);
    if ("previousEditPage".equals(method)) return previousEditPage(sdata);
    if ("nextEditPage".equals(method)) return nextEditPage(sdata);
    if ("toEditPage".equals(method)) return toEditPage(rdata, sdata);
    if ("openImageSelector".equals(method)) return openImageSelector(rdata, sdata);
    if ("previousSelectPage".equals(method)) return previousSelectPage(sdata);
    if ("nextSelectPage".equals(method)) return nextSelectPage(sdata);
    if ("toSelectPage".equals(method)) return toSelectPage(rdata, sdata);
    if ("editSelectedImage".equals(method)) return editSelectedImage(rdata, sdata);
    if ("openImageUpload".equals(method)) return openImageUpload(rdata, sdata);
    if ("uploadImage".equals(method)) return uploadImage(rdata, sdata);
    if ("openChangeImage".equals(method)) return openChangeImage(rdata, sdata);
    if ("updateImage".equals(method)) return updateImage(rdata, sdata);
    if ("openDeleteImage".equals(method)) return openDeleteImage(rdata, sdata);
    if ("deleteImage".equals(method)) return deleteImage(rdata, sdata);
    return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
  }

  public Response openEditImages(SessionData sdata) throws Exception {
    ImageSelectData data = new ImageSelectData();
    data.setImages(ImageBean.getInstance().getImageList(true));
    sdata.setParam("imageSelect", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageEditAll.jsp");
  }

  public Response previousEditPage(SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    data.setPreviousPage();
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageEditAll.jsp");
  }

  public Response nextEditPage(SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    data.setNextPage();
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageEditAll.jsp");
  }

  public Response toEditPage(RequestData rdata,SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    int page=rdata.getParamInt("page");
    data.setPage(page);
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageEditAll.jsp");
  }

  public Response openImageSelector(RequestData rdata,SessionData sdata) throws Exception {
    ImageSelectData data = new ImageSelectData();
    data.setItemsPerPage(3);
    data.setForHtmlEditor(rdata.getParamBoolean("forCk"));
    data.setImages(ImageBean.getInstance().getImageList(false));
    sdata.setParam("imageSelect", data);
    return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageSelect.jsp");
  }

  public Response previousSelectPage(SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    data.setPreviousPage();
    return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageSelect.jsp");
  }

  public Response nextSelectPage(SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    data.setNextPage();
    return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageSelect.jsp");
  }

  public Response toSelectPage(RequestData rdata,SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    int page=rdata.getParamInt("page");
    data.setPage(page);
    return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageSelect.jsp");
  }

  public Response editSelectedImage(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("iid", -1);
    ImageData idata = ImageBean.getInstance().getImageData(id);
    sdata.setParam("image", idata);
    return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageEditSelected.jsp");
  }

  public Response openImageUpload(RequestData rdata, SessionData sdata) throws Exception {
    ImageData idata = new ImageData();
    boolean popup=rdata.getParamBoolean("popup");
    idata.setId(ImageBean.getInstance().getNextId());
    idata.setBeingCreated(true);
    sdata.setParam("image", idata);
    return new JspResponse(popup ? "/_jsp/popupmaster.jsp" : "/_jsp/master.jsp","/_jsp/imageUpload.jsp");
  }

  public Response uploadImage(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    boolean popup=rdata.getParamBoolean("popup");
    if (data == null)
      return new MsgResponse(popup ? "/_jsp/popupmaster.jsp" : "/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    ImageData idata = (ImageData) sdata.getParam("image");
    if (idata == null)
      return openImageUpload(rdata, sdata);
    if (!readImageRequestData(idata, rdata))
      return new JspResponse(popup ? "/_jsp/popupmaster.jsp" : "/_jsp/master.jsp","/_jsp/imageUpload.jsp");
    ImageBean.getInstance().saveImageData(idata);
    if (data.getImages() != null)
      data.getImages().add(0, idata);
    if (popup)
      return new JspResponse("/_jsp/popupmaster.jsp","/_jsp/imageSelect.jsp");
    else
      return new JspResponse("/_jsp/master.jsp","/_jsp/imageEditAll.jsp");
  }

  public Response openChangeImage(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null) {
      data = new ImageSelectData();
      sdata.setParam("imageSelect", data);
    }
    ArrayList<Integer> ids = rdata.getParamIntegerList("iid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditImages(sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, Strings.getHtml("singleselection"));
      return openEditImages(sdata);
    }
    ImageData idata = ImageBean.getInstance().getImageData(ids.get(0));
    sdata.setParam("image", idata);
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageUpdate.jsp");
  }

  public Response updateImage(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    ImageData idata = (ImageData) sdata.getParam("image");
    if (idata == null)
      return openChangeImage(rdata, sdata);
    if (!readImageRequestData(idata, rdata))
      return new JspResponse("/_jsp/master.jsp","/_jsp/imageUpdate.jsp");
    ImageBean.getInstance().saveImageData(idata);
    if (data.getImages() != null) {
      for (int i = 0; i < data.getImages().size(); i++) {
        ImageData img = data.getImages().get(i);
        if (img.getId() == idata.getId()) {
          idata.copyMetaData(img);
          break;
        }
      }
    }
    return openEditImages(sdata);
  }

  public boolean readImageRequestData(ImageData data, RequestData rdata) {
    FileData file = rdata.getParamFile("image");
    RequestError err=new RequestError();
    if (file == null || file.getBytes() == null || file.getName().length() == 0 ||
        file.getContentType() == null || file.getContentType().length() == 0) {
      err.addErrorString(Strings.getHtml("notcomplete"));
      return false;
    }
    data.setBytes(file.getBytes());
    data.setSize(data.getBytes().length);
    data.setImageName(file.getName());
    ImageTool.setContentType(data);
    BufferedImage image;
    try {
      image = ImageTool.createImage(data);
      ImageTool.createJpegThumbnail(data, image, ImageController.MAX_WIDTH, ImageController.MAX_HEIGHT);
    }
    catch (Exception e) {
      data.setImageName("");
      err.addErrorString(Strings.getHtml("notcomplete"));
    }
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response openDeleteImage(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("iid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditImages(sdata);
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/imageDelete.jsp");
  }

  public Response deleteImage(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("iid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditImages(sdata);
    }
    for (Integer id : ids) {
      ImageBean.getInstance().deleteImage(id);
    }
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    ImageData idata;
    if (data != null && data.getImages() != null) {
      for (int i = 0; i < data.getImages().size(); i++) {
        idata = data.getImages().get(i);
        if (ids.contains(idata.getId())) {
          data.getImages().remove(i);
          break;
        }
      }
    }
    return openEditImages(sdata);
  }


	public Response show(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("iid");
    FileData data = ImageBean.getInstance().getImageFromCache(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown image - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

  public Response showThumbnail(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("iid");
    FileData data = ImageBean.getInstance().getThumbnail(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown thumbnail - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

}
