/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.communication;

import de.bandika.base.*;
import de.bandika.http.SessionData;
import de.bandika.http.RequestData;
import de.bandika.http.Response;
import de.bandika.page.fields.BlogEntry;
import de.bandika.page.fields.BlogField;
import de.bandika.page.PageController;
import de.bandika.page.PageData;
import de.bandika.page.PageBean;
import de.bandika.page.ParagraphData;

import java.util.Date;

/**
 * Class CommunicationController is the controller class for mail, blogs, forums etc. <br>
 * Usage:
 */
public class CommunicationController extends Controller {

	public static String KEY_COMMUNICATION = "comm";

	public static String MAIL_HOST = "mailHost";
	public static String MAIL_SENDER = "mailSender";
	public static String MAIL_RECEIVER = "mailReceiver";

	public CommunicationBean getCommunicationBean() {
    return (CommunicationBean) Bean.getBean(KEY_COMMUNICATION);
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (method.equals("sendMail")) return sendMail(rdata, sdata);
    if (method.equals("addBlogEntry")) return addBlogEntry(rdata, sdata);
    if (method.equals("removeBlogEntry")) return removeBlogEntry(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  public Response sendMail(RequestData rdata, SessionData sdata) throws Exception {
    Mailer mailer = new Mailer();
    mailer.setSmtpHost(BaseConfig.getConfig(MAIL_HOST));
    mailer.setFromAddress(rdata.getParamString("mailform_email"));
    mailer.setSubject(rdata.getParamString("mailform_subject"));
    mailer.setContent(rdata.getParamString("mailform_text"));
    mailer.setToAddress(rdata.getParamString("mailform_receiver"));
    if (mailer.isComplete()) {
      try{
        mailer.sendMail();
        rdata.setParam("msg", UserStrings.messagesent);
      }
      catch (Exception e){
        rdata.setError(new RequestError(UserStrings.messagenotsent+e.getMessage()));
      }
    } else {
      RequestError err = new RequestError();
      err.addErrorString(UserStrings.notcomplete);
      rdata.setError(err);
    }
    PageController pc = (PageController) Controller.getController(PageController.KEY_PAGE);
    return pc.show(rdata, sdata);
  }

  public Response addBlogEntry(RequestData rdata, SessionData sdata) throws Exception {
    BlogEntry entry = new BlogEntry();
    entry.setName(rdata.getParamString("blogform_name"));
    entry.setEmail(rdata.getParamString("blogform_email"));
    entry.setText(rdata.getParamString("blogform_text"));
    entry.setTime(new Date());
    if (entry.isComplete()) {
      int id = rdata.getParamInt("id");
      int pid = rdata.getParamInt("pid");
      int idx = rdata.getParamInt("idx");
      PageBean bean = (PageBean) Bean.getBean(PageController.KEY_PAGE);
      PageData data = bean.getPage(id);
      data.prepareEditing();
      ParagraphData pdata = data.getParagraphById(pid);
      BlogField field = (BlogField) pdata.getFields().get(idx);
      field.addEntry(entry);
      data.prepareSave();
      bean.savePage(data);
    } else {
      RequestError err = new RequestError();
      err.addErrorString(UserStrings.notcomplete);
      rdata.setError(err);
      rdata.setParam("newEntry", entry);
    }
    PageController pc = (PageController) Controller.getController(PageController.KEY_PAGE);
    return pc.show(rdata, sdata);
  }

  public Response removeBlogEntry(RequestData rdata, SessionData sdata) throws Exception {
    int pid = rdata.getParamInt("pid");
    int idx = rdata.getParamInt("idx");
    int entryIdx = rdata.getParamInt("entryIdx");
    PageData data = (PageData) sdata.getParam("pageData");
    ParagraphData pdata = data.getParagraphById(pid);
    BlogField field = (BlogField) pdata.getFields().get(idx);
    field.getEntries().remove(entryIdx);
    PageController pc = (PageController) Controller.getController(PageController.KEY_PAGE);
    return pc.cancelEditParagraph(rdata, sdata);
  }

}