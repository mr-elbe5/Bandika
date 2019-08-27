package de.elbe5.servlet;

import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.jsppage.JspPageData;
import de.elbe5.request.*;
import de.elbe5.request.*;

public abstract class Controller {

    public abstract String getKey();

    protected IActionResult showHome() {
        return new RedirectActionResult("/");
    }

    protected IActionResult forbidden(RequestData rdata) {
        rdata.setMessage(Strings.string("_forbidden",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new ForwardActionResult("/ctrl/user/openLogin");
    }

    protected IActionResult notFound() {
        return new ErrorActionResult(ResponseCode.NOT_FOUND);
    }

    protected IActionResult methodNotAllowed() {
        return new ErrorActionResult(ResponseCode.METHOD_NOT_ALLOWED);
    }

    protected IActionResult noData(RequestData rdata) {
        rdata.setMessage(Strings.string("_noData",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new RedirectActionResult("/ctrl/user/openLogin");
    }

    protected IActionResult badData(RequestData rdata) {
        rdata.setMessage(Strings.string("_badData",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new RedirectActionResult("/ctrl/user/openLogin");
    }

    protected IActionResult openAdminPage(RequestData rdata, String jsp, String title) {
        rdata.put(Statics.KEY_JSP, jsp);
        rdata.put(Statics.KEY_TITLE, title);
        return new ForwardActionResult("/WEB-INF/_jsp/administration/adminMaster.jsp");
    }

    protected IActionResult openJspPage(String jsp) {
        JspPageData pageData = new JspPageData();
        pageData.setJsp(jsp);
        return new PageActionResult(pageData);
    }
}
