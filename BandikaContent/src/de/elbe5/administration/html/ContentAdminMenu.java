package de.elbe5.administration.html;

import de.elbe5.request.RequestData;

import java.util.Map;

public class ContentAdminMenu extends AdminMenu {

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendNavStart(sb, rdata);
        appendSystemNav(sb, rdata);
        appendUserNav(sb, rdata);
        appendContentNav(sb, rdata);
        appendLogNav(sb, rdata);
        appendNavEnd(sb);

    }

    public void appendContentNav(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(sb,"""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentAdministration">$contentAdministration$
                        </a>
                    </li>""",
                    Map.ofEntries(
                            param("contentAdministration","_contentAdministration")
                    )
            );
        }
    }

    public void appendLogNav(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(sb,"""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentLog">$contentLog$
                        </a>
                    </li>""",
                    Map.ofEntries(
                            param("contentLog","_contentLog")
                    )
            );
        }
    }

}
