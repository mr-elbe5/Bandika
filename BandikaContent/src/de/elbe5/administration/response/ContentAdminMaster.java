package de.elbe5.administration.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ContentAdminMaster extends AdminMaster {

    public ContentAdminMaster(IAdminPage include, String title){
        super(include, title);
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        StringBuilder sb = new StringBuilder();
        appendHtmlStart(sb, rdata);
        appendNavStart(sb);
        appendSystemNav(sb, rdata);
        appendUserNav(sb, rdata);
        appendContentNav(sb, rdata);
        appendLogNav(sb, rdata);
        appendNavEnd(sb);
        appendHeaderEnd(sb);
        if (include != null) {
            include.appendHtml(sb, rdata);
        }
        appendHtmlEnd(sb);
        html=sb.toString();
        sendHtml(response);
    }

    public void appendContentNav(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            sb.append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentAdministration">{1}
                        </a>
                    </li>""",
                    Strings.getHtml("_contentAdministration")
            ));
        }
    }

    public void appendLogNav(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            sb.append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentLog">{1}
                        </a>
                    </li>""",
                    Strings.getHtml("_contentLog")
            ));
        }
    }

}
