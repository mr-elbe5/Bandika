package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;

public abstract class Controller {

    public abstract String getKey();

    protected IActionResult showHome() {
        return new RedirectActionResult("/");
    }

    protected IActionResult forbidden(RequestData rdata) {
        rdata.setMessage(Strings._forbidden.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new ForwardActionResult("/user/openLogin");
    }

    protected IActionResult notFound() {
        return new ErrorActionResult(ResponseCode.NOT_FOUND);
    }

    protected IActionResult methodNotAllowed() {
        return new ErrorActionResult(ResponseCode.METHOD_NOT_ALLOWED);
    }

    protected IActionResult noData(RequestData rdata) {
        rdata.setMessage(Strings._noData.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new RedirectActionResult("/user/openLogin");
    }

    protected IActionResult badData(RequestData rdata) {
        rdata.setMessage(Strings._badData.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        return new RedirectActionResult("/user/openLogin");
    }
}
