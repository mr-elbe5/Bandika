/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.image;

import de.net25.base.resources.FileData;
import de.net25.base.Logger;
import de.net25.base.exception.RightException;
import de.net25.base.controller.*;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

/**
 * Class ImageController is the controller class for image files. <br>
 * Usage:
 */
public class ImageController extends Controller {

  public static final String imageEditAllJsp = "/jsps/resources/image/editImages.jsp";
  public static final String imageEditSelectedJsp = "/jsps/resources/image/editSelected.jsp";
  public static final String imageSelectJsp = "/jsps/resources/image/select.jsp";
  public static final String imageUploadJsp = "/jsps/resources/image/upload.jsp";
  public static final String imageUploadSelectJsp = "/jsps/resources/image/uploadSelect.jsp";
  public static final String imageUploadOkJsp = "/jsps/resources/image/uploadOk.jsp";
  public static final String imageUploadSelectOkJsp = "/jsps/resources/image/uploadSelectOk.jsp";
  public static final String imageUpdateJsp = "/jsps/resources/image/update.jsp";
  public static final String imageDeleteJsp = "/jsps/resources/image/delete.jsp";
  public static final String imageDeleteAllJsp = "/jsps/resources/image/deleteAll.jsp";

  /**
   * Method getImageBean returns the imageBean of this ImageController object.
   *
   * @return the imageBean (type ImageBean) of this ImageController object.
   */
  public ImageBean getImageBean() {
    return (ImageBean) Statics.getBean(Statics.KEY_IMAGE);
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
    if ("showThumbnail".equals(method)) return showThumbnail(rdata);
    if (!sdata.isEditor())
      throw new RightException();
    if ("openEditImages".equals(method)) return openEditImages(sdata);
    if ("reopenEditImages".equals(method)) return reopenEditImages(rdata, sdata);
    if ("openFieldImageSelector".equals(method)) return openFieldImageSelector(sdata);
    if ("openFckImageSelector".equals(method)) return openFckImageSelector(sdata);
    if ("reopenSelectImages".equals(method)) return reopenSelectImages(rdata, sdata);
    if ("editSelectedImage".equals(method)) return editSelectedImage(rdata, sdata);
    if ("openImageUpload".equals(method)) return openImageUpload(sdata);
    if ("openImageUploadSelect".equals(method)) return openImageUploadSelect(sdata);
    if ("uploadImage".equals(method)) return uploadImage(rdata, sdata);
    if ("uploadImageSelect".equals(method)) return uploadImageSelect(rdata, sdata);
    if ("openImageUpdate".equals(method)) return openImageUpdate(rdata, sdata);
    if ("updateImage".equals(method)) return updateImage(rdata, sdata);
    if ("openDeleteImage".equals(method)) return openDeleteImage(rdata, sdata);
    if ("deleteImage".equals(method)) return deleteImage(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method openEditImages
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditImages(SessionData sdata) throws Exception {
    ImageSelectData data = new ImageSelectData();
    data.setImages(getImageBean().getImageList());
    sdata.setParam("imageSelect", data);
    return new PageResponse(Strings.getString("imageAdministration", sdata.getLocale()), "", imageEditAllJsp);
  }

  /**
   * Method reopenEditImages
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response reopenEditImages(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return noData(rdata, sdata);
    data.setImages(getImageBean().getImageList());
    return new PageResponse(Strings.getString("imageAdministration", sdata.getLocale()), "", imageEditAllJsp);
  }

  /**
   * Method show
   *
   * @param rdata of type RequestData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response show(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("iid");
    FileData data = getImageBean().getImageFromCache(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown image - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

  /**
   * Method showThumbnail
   *
   * @param rdata of type RequestData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response showThumbnail(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("iid");
    FileData data = getImageBean().getThumbnail(id);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown thumbnail - id: " + id);
      return null;
    }
    return new BinaryResponse(data.getName(), data.getContentType(), data.getBytes());
  }

  /**
   * Method openFieldImageSelector
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openFieldImageSelector(SessionData sdata) throws Exception {
    ImageSelectData data = new ImageSelectData();
    data.setForFck(false);
    data.setImages(getImageBean().getImageList());
    sdata.setParam("imageSelect", data);
    return new PopupResponse(Strings.getString("imageSelect", sdata.getLocale()), imageSelectJsp);
  }

  /**
   * Method openFckImageSelector
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openFckImageSelector(SessionData sdata) throws Exception {
    ImageSelectData data = new ImageSelectData();
    data.setForFck(true);
    data.setImages(getImageBean().getImageList());
    sdata.setParam("imageSelect", data);
    return new PopupResponse(Strings.getString("imageSelect", sdata.getLocale()), imageSelectJsp);
  }

  /**
   * Method reopenSelectImages
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response reopenSelectImages(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    data.setImages(getImageBean().getImageList());
    return new PopupResponse(Strings.getString("imageSelect", sdata.getLocale()), imageSelectJsp);
  }

  /**
   * Method editSelectedImage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response editSelectedImage(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("iid", -1);
    ImageData idata = getImageBean().getImageData(id);
    sdata.setParam("image", idata);
    return new PopupResponse(Strings.getString("imageUpdate", sdata.getLocale()), imageEditSelectedJsp);
  }

  /**
   * Method openImageUpload
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openImageUpload(SessionData sdata) throws Exception {
    ImageData idata = new ImageData();
    idata.setId(getImageBean().getNextId());
    idata.setBeingCreated(true);
    sdata.setParam("image", idata);
    return new PageResponse(Strings.getString("imageUpload", sdata.getLocale()), "", imageUploadJsp);
  }

  /**
   * Method openImageUploadSelect
   *
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openImageUploadSelect(SessionData sdata) throws Exception {
    ImageData idata = new ImageData();
    idata.setId(getImageBean().getNextId());
    idata.setBeingCreated(true);
    sdata.setParam("image", idata);
    return new PopupResponse(Strings.getString("imageUpload", sdata.getLocale()), imageUploadSelectJsp);
  }

  /**
   * Method uploadImage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response uploadImage(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    ImageData idata = (ImageData) sdata.getParam("image");
    if (idata == null)
      return openImageUpload(sdata);
    if (!idata.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("imageUpload", sdata.getLocale()), "", imageUploadJsp);
    getImageBean().saveImageData(idata);
    if (data.getImages() != null)
      data.getImages().add(0, idata);
    return new PageResponse(Strings.getString("imageUploadOk", sdata.getLocale()), "", imageUploadOkJsp);
  }

  /**
   * Method uploadImageSelect
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response uploadImageSelect(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return noPopupData(rdata, sdata);
    ImageData idata = (ImageData) sdata.getParam("image");
    if (idata == null)
      return openImageUpload(sdata);
    if (!idata.readRequestData(rdata, sdata))
      return new PopupResponse(Strings.getString("imageUpload", sdata.getLocale()), imageUploadSelectJsp);
    getImageBean().saveImageData(idata);
    if (data.getImages() != null)
      data.getImages().add(0, idata);
    return new PopupResponse(Strings.getString("imageUploadOk", sdata.getLocale()), imageUploadSelectOkJsp);
  }

  /**
   * Method openImageUpdate
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openImageUpdate(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null) {
      data = new ImageSelectData();
      sdata.setParam("imageSelect", data);
    }
    int id = rdata.getParamInt("iid", -1);
    ImageData idata = getImageBean().getImageData(id);
    sdata.setParam("image", idata);
    return new PageResponse(Strings.getString("imageUpdate", sdata.getLocale()), "", imageUpdateJsp);
  }

  /**
   * Method updateImage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response updateImage(RequestData rdata, SessionData sdata) throws Exception {
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    if (data == null)
      return noData(rdata, sdata);
    ImageData idata = (ImageData) sdata.getParam("image");
    if (idata == null)
      return openImageUpdate(rdata, sdata);
    if (!idata.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("imageUpdate", sdata.getLocale()), "", imageUpdateJsp);
    getImageBean().saveImageData(idata);
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

  /**
   * Method openDeleteImage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDeleteImage(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("iid");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return openEditImages(sdata);
    }
    if (getImageBean().isImageInUse(id)) {
      addError(rdata, Strings.getString("err_image_in_use", sdata.getLocale()));
      return openEditImages(sdata);
    }
    return new PageResponse(Strings.getString("imageDelete", sdata.getLocale()), "", imageDeleteJsp);
  }

  /**
   * Method deleteImage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response deleteImage(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("iid");
    getImageBean().deleteImage(id);
    ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
    ImageData idata;
    if (data != null && data.getImages() != null) {
      for (int i = 0; i < data.getImages().size(); i++) {
        idata = data.getImages().get(i);
        if (idata.getId() == id) {
          data.getImages().remove(i);
          break;
        }
      }
    }
    return openEditImages(sdata);
  }

}
