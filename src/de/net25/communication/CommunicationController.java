/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.communication;

import de.net25.base.controller.Controller;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.base.controller.Response;
import de.net25.base.Mailer;
import de.net25.base.RequestError;
import de.net25.http.SessionData;
import de.net25.http.RequestData;
import de.net25.content.ContentController;
import de.net25.content.ContentData;
import de.net25.content.ContentBean;
import de.net25.content.ParagraphData;
import de.net25.content.fields.BlogEntry;
import de.net25.content.fields.BlogField;

import java.util.Date;

/**
 * Class CommunicationController is the controller class for mail, blogs, forums etc. <br>
 * Usage:
 */
public class CommunicationController extends Controller {


  /**
   * Method getUserBean returns the userBean of this UserController object.
   *
   * @return the userBean (type UserBean) of this UserController object.
   */
  public CommunicationBean getCommunicationBean() {
    return (CommunicationBean) Statics.getBean(Statics.KEY_COMMUNICATION);
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
    if (method.equals("sendMail")) return sendMail(rdata, sdata);
    if (method.equals("addBlogEntry")) return addBlogEntry(rdata, sdata);
    if (method.equals("removeBlogEntry")) return removeBlogEntry(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method sendMail
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response sendMail(RequestData rdata, SessionData sdata) throws Exception {
    Mailer mailer = new Mailer();
    mailer.setSmtpHost(Statics.MAIL_SERVER);
    mailer.setFromAddress(rdata.getParamString("mailform_email"));
    mailer.setSubject(rdata.getParamString("mailform_subject"));
    mailer.setContent(rdata.getParamString("mailform_text"));
    mailer.setToAddress(rdata.getParamString("mailform_receiver"));
    if (mailer.isComplete()) {
      try{
        mailer.sendMail();
        rdata.setParam("msg", Strings.getString("messageSent", sdata.getLocale()));
      }
      catch (Exception e){
        rdata.setError(new RequestError(Strings.getString("messageNotSent", sdata.getLocale())+e.getMessage()));
      }
    } else {
      RequestError err = new RequestError();
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      rdata.setError(err);
    }
    ContentController cc = (ContentController) Statics.getController(Statics.KEY_CONTENT);
    return cc.show(rdata, sdata);
  }

  /**
   * Method addBlogEntry ...
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when
   */
  public Response addBlogEntry(RequestData rdata, SessionData sdata) throws Exception {
    BlogEntry entry = new BlogEntry();
    entry.setName(rdata.getParamString("blogform_name"));
    entry.setEmail(rdata.getParamString("blogform_email"));
    entry.setText(rdata.getParamString("blogform_text"));
    entry.setTime(new Date());
    if (entry.isComplete()) {
      int id = rdata.getParamInt("id");
      int pid = rdata.getParamInt("pid");
      String fieldName = rdata.getParamString("fieldName");
      ContentBean bean = (ContentBean) Statics.getBean(Statics.KEY_CONTENT);
      ContentData data = bean.getContent(id);
      data.prepareEditing();
      ParagraphData pdata = data.getParagraphById(pid);
      BlogField field = (BlogField) pdata.getField(fieldName);
      field.addEntry(entry);
      data.prepareSave();
      bean.saveContent(data);
    } else {
      RequestError err = new RequestError();
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      rdata.setError(err);
      rdata.setParam("newEntry", entry);
    }
    ContentController cc = (ContentController) Statics.getController(Statics.KEY_CONTENT);
    return cc.show(rdata, sdata);
  }

  /**
   * Method removeBlogEntry ...
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when
   */
  public Response removeBlogEntry(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("id");
    int pid = rdata.getParamInt("pid");
    String fieldName = rdata.getParamString("fieldName");
    int idx = rdata.getParamInt("entryIdx");
    ContentData data = (ContentData) sdata.getParam("contentData");
    ParagraphData pdata = data.getParagraphById(pid);
    BlogField field = (BlogField) pdata.getField(fieldName);
    field.getEntries().remove(idx);
    ContentController cc = (ContentController) Statics.getController(Statics.KEY_CONTENT);
    return cc.cancelEditParagraph(rdata, sdata);
  }

}