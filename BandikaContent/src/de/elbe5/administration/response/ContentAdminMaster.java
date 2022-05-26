package de.elbe5.administration.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ContentAdminMaster extends AdminMaster {

    public ContentAdminMaster(AdminPage include, String title){
        super(include, title);
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        StringBuilder sb = new StringBuilder();
        appendHtmlStart(rdata);
        appendNavStart();
        appendSystemNav(rdata);
        appendUserNav(rdata);
        appendContentNav(rdata);
        appendLogNav(rdata);
        appendNavEnd();
        appendHeaderEnd();
        if (include != null) {
            include.appendHtml(rdata);
        }
        appendHtmlEnd();
        sendHtml(response);
    }

    public void appendContentNav(RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentAdministration">{1}
                        </a>
                    </li>""",
                    Strings.getHtml("_contentAdministration")
            ));
        }
    }

    public void appendLogNav(RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentLog">{1}
                        </a>
                    </li>""",
                    Strings.getHtml("_contentLog")
            ));
        }
    }

}
