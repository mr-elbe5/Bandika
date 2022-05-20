/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.application.Configuration;
import de.elbe5.application.MailHelper;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentController;
import de.elbe5.page.response.AddPagePart;
import de.elbe5.request.*;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.IResponse;

public class PageController extends ContentController {

    public static final String KEY = "page";

    private static PageController instance = null;

    public static void setInstance(PageController instance) {
        PageController.instance = instance;
    }

    public static PageController getInstance() {
        return instance;
    }

    public static void register(PageController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse addPart(RequestData rdata) {
        int contentId = rdata.getId();
        PageData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, PageData.class);
        checkRights(data.hasUserEditRight(rdata));
        int fromPartId = rdata.getAttributes().getInt("fromPartId", -1);
        String partType = rdata.getAttributes().getString("partType");
        PagePartData pdata = PagePartFactory.getNewData(partType);
        pdata.setCreateValues(rdata);
        data.addPart(pdata, fromPartId, true);
        rdata.getAttributes().put(PagePartData.KEY_PART, pdata);
        return new AddPagePart();
    }

    public IResponse sendContact(RequestData rdata) {
        String captcha = rdata.getAttributes().getString("captcha");
        String sessionCaptcha = rdata.getSessionObject(RequestKeys.KEY_CAPTCHA, String.class);
        if (!captcha.equals(sessionCaptcha)){
            rdata.addFormField("captcha");
            rdata.addFormError(Strings.getString("_captchaError"));
            return show(rdata);
        }
        String name = rdata.getAttributes().getString("contactName");
        String email = rdata.getAttributes().getString("contactEmail");
        String message = rdata.getAttributes().getString("contactMessage");
        if (name.isEmpty()) {
            rdata.addIncompleteField("contactName");
        }
        if (email.isEmpty()) {
            rdata.addIncompleteField("contactEmail");
        }
        if (message.isEmpty()) {
            rdata.addIncompleteField("contactMessage");
        }
        if (!rdata.checkFormErrors()){
            return show(rdata);
        }
        message = String.format(Strings.getHtml("_contactRequestText"),name,email) + message;
        if (!MailHelper.sendPlainMail(Configuration.getMailReceiver(), Strings.getString("_contactRequest"), message)) {
            rdata.setMessage(Strings.getString("_contactRequestError"), RequestKeys.MESSAGE_TYPE_ERROR);
            return show(rdata);
        }
        rdata.setMessage(Strings.getString("_contactRequestSent"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        rdata.removeSessionObject(RequestKeys.KEY_CAPTCHA);
        return show(rdata);
    }

}
