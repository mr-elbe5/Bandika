package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

public class AdminSystemNavTag extends TemplateTag {

    public static final String TYPE = "adminSystemNav";

    public AdminSystemNavTag() {
        this.type = TYPE;
    }

    static final String html = """
            <li class="nav-item">
                <a class="nav-link" href="/ctrl/admin/openSystemAdministration">{{_systemAdministration}}</a>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            append(sb, html, null);
        }
    }

}
