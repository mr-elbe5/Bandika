package de.elbe5.servlet;

import de.elbe5.base.LocalizedStrings;
import de.elbe5.request.*;
import de.elbe5.response.ForwardResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ServerPageResponse;

import javax.servlet.http.HttpServletResponse;

public abstract class Controller {

    public abstract String getKey();

    protected IResponse showHome() {
        return new ForwardResponse("/");
    }

    protected void checkRights(boolean hasRights){
        if (!hasRights)
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    protected void setSaveError(RequestData rdata) {
        rdata.setMessage(LocalizedStrings.string("_saveError"), RequestKeys.MESSAGE_TYPE_ERROR);
    }

    protected IResponse openAdminPage(RequestData rdata, String jsp, String title) {
        rdata.getAttributes().put(RequestKeys.KEY_JSP, jsp);
        rdata.getAttributes().put(RequestKeys.KEY_TITLE, title);
        return new ServerPageResponse("administration/adminMaster");
    }

    protected IResponse showSystemAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/systemAdministration", LocalizedStrings.string("_systemAdministration"));
    }

    protected IResponse showPersonAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/personAdministration", LocalizedStrings.string("_personAdministration"));
    }

    protected IResponse showContentAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentAdministration", LocalizedStrings.string("_contentAdministration"));
    }

    protected IResponse showContentLog(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentLog", LocalizedStrings.string("_contentLog"));
    }
}
