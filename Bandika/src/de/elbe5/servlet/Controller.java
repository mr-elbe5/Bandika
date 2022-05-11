package de.elbe5.servlet;

import de.elbe5.application.AdminPage;
import de.elbe5.application.Configuration;
import de.elbe5.base.Strings;
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
        rdata.setMessage(Strings.getString("_saveError"), RequestKeys.MESSAGE_TYPE_ERROR);
    }

    protected IResponse openAdminPage(RequestData rdata, String include, String title) {
        rdata.getTemplateAttributes().put("title", title);
        rdata.getTemplateAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getTemplateAttributes().put("hasSystemRights", rdata.hasSystemRight(SystemZone.APPLICATION));
        rdata.getTemplateAttributes().put("hasUserRights", rdata.hasSystemRight(SystemZone.USER));
        rdata.getTemplateAttributes().put("hasContentRights", rdata.hasAnyContentRight());
        return new MasterResponse("adminMaster", new AdminPage());
    }

    protected IResponse showSystemAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/systemAdministration", Strings.getString("_systemAdministration"));
    }

    protected IResponse showUserAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/userAdministration", Strings.getString("_userAdministration"));
    }

    protected IResponse showContentAdministration(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentAdministration", Strings.getString("_contentAdministration"));
    }

    protected IResponse showContentLog(RequestData rdata) {
        return openAdminPage(rdata, "administration/contentLog", Strings.getString("_contentLog"));
    }
}
