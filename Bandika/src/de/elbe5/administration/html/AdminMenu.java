package de.elbe5.administration.html;

import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;
import de.elbe5.rights.SystemZone;

import java.util.Map;

public class AdminMenu implements IHtmlBuilder {

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendNavStart(sb, rdata);
        appendSystemNav(sb, rdata);
        appendUserNav(sb, rdata);
        appendNavEnd(sb);
    }

    public void appendNavStart(StringBuilder sb, RequestData rdata) {
        append(sb, """
                        <div class="menu row">
                            <section class="col-12 menu">
                                <nav class="navbar navbar-expand-lg">
                                    <span class="navbar-brand" admin-logo"><img class="admin-logo" src="/static-content/img/admin-logo.png" > - $administration$</span>
                                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                                        <span class="navbar-toggler-icon"></span>
                                    </button>
                                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                                        <ul class="navbar-nav mr-auto">
                                        """,
                Map.ofEntries(
                        param("administration","_administration")
                )
        );

    }

    public void appendSystemNav(StringBuilder sb, RequestData rdata) {
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            append(sb, """
                                <li class="nav-item">
                                    <a class="nav-link"
                                       href="/ctrl/admin/openSystemAdministration">$systemAdministration$
                                    </a>
                                </li>
                            """,
                    Map.ofEntries(
                            param("systemAdministration","_systemAdministration")
                    )
            );
        }
    }

    public void appendUserNav(StringBuilder sb,RequestData rdata) {
        if (rdata.hasSystemRight(SystemZone.USER)) {
            append(sb, """
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="/ctrl/admin/openUserAdministration">$userAdministration$
                                </a>
                            </li>
                            """,
                    Map.ofEntries(
                            param("userAdministration","_userAdministration")
                    )
            );
        }
    }

    public void appendNavEnd(StringBuilder sb) {
        append(sb, """
                                </ul>
                            </div>
                        </nav>
                    </section>
                </div>
                """);
    }

}
