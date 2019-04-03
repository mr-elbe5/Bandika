package de.elbe5.cms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IActionResult {

    void processAction(ServletContext context, RequestData rdata, HttpServletResponse response);

}
