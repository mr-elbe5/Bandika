package de.elbe5.json.servlet;

import de.elbe5.servlet.ResponseException;

import javax.servlet.http.HttpServletResponse;

public abstract class JsonController {

    public abstract String getKey();

    protected void checkRights(boolean hasRights){
        if (!hasRights)
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
