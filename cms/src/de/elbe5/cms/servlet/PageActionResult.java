package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;
import de.elbe5.cms.page.PageData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class PageActionResult extends ForwardActionResult{

    private PageData data;

    public PageActionResult(PageData data){
        super("/WEB-INF/_jsp/page/page.jsp");
        this.data=data;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        rdata.put(Statics.KEY_PAGE, data);
        super.processAction(context,rdata,response);
    }
}
