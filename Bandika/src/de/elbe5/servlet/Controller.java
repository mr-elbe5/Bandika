package de.elbe5.servlet;

import de.elbe5.base.Strings;
import de.elbe5.request.*;
import de.elbe5.response.*;

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

}
