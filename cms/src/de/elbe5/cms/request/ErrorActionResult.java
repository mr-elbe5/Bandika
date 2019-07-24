package de.elbe5.cms.request;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ErrorActionResult implements IActionResult {

    private int resultCode;

    public ErrorActionResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        response.setStatus(resultCode);
    }
}
