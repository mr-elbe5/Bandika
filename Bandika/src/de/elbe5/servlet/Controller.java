package de.elbe5.servlet;

import de.elbe5.application.Configuration;
import de.elbe5.base.LocalizedStrings;
import de.elbe5.request.*;
import de.elbe5.response.*;
import de.elbe5.rights.SystemZone;

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

    protected IResponse openAdminPage(RequestData rdata, String include, String title) {
        rdata.getPageAttributes().put("title", title);
        rdata.getPageAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getPageAttributes().put("hasSystemRights", rdata.hasSystemRight(SystemZone.APPLICATION));
        rdata.getPageAttributes().put("hasUserRights", rdata.hasSystemRight(SystemZone.USER));
        rdata.getPageAttributes().put("hasContentRights", rdata.hasAnyContentRight());
        return new MasterResponse("adminMaster", new ServerPageInclude(include));
    }

    protected IResponse showSystemAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/systemAdministration", LocalizedStrings.string("_systemAdministration"));
    }

    protected IResponse showUserAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/userAdministration", LocalizedStrings.string("_userAdministration"));
    }

    protected IResponse showContentAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentAdministration", LocalizedStrings.string("_contentAdministration"));
    }

    protected IResponse showContentLog(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentLog", LocalizedStrings.string("_contentLog"));
    }
}
