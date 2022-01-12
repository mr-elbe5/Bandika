package de.elbe5.servlet;

import de.elbe5.base.data.Strings;
import de.elbe5.request.*;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;

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
        rdata.setMessage(Strings.string("_saveError",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
    }

    protected IResponse openAdminPage(RequestData rdata, String jsp, String title) {
        rdata.put(RequestKeys.KEY_JSP, jsp);
        rdata.put(RequestKeys.KEY_TITLE, title);
        return new ForwardResponse("/WEB-INF/_jsp/administration/adminMaster.jsp");
    }

    protected IResponse showSystemAdministration(RequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings.string("_systemAdministration",rdata.getLocale()));
    }

    protected IResponse showPersonAdministration(RequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/personAdministration.jsp", Strings.string("_personAdministration",rdata.getLocale()));
    }

    protected IResponse showContentAdministration(RequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/contentAdministration.jsp", Strings.string("_contentAdministration",rdata.getLocale()));
    }

    protected IResponse showContentLog(RequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/contentLog.jsp", Strings.string("_contentLog",rdata.getLocale()));
    }
}
