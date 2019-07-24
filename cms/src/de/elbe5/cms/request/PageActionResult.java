package de.elbe5.cms.request;

import de.elbe5.cms.page.PageData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class PageActionResult extends ForwardActionResult {

    private PageData data;

    public PageActionResult(PageData data) {
        super("/WEB-INF/_jsp/page/page.jsp");
        this.data = data;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        rdata.setCurrentPage(data);
        super.processAction(context, rdata, response);
    }
}
