package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

public class AdminUserNavTag extends TemplateTag {

    public static final String TYPE = "adminUserNav";

    public AdminUserNavTag() {
        this.type = TYPE;
    }

    static final String html = """
            <li class="nav-item">
                <a class="nav-link" href="/ctrl/admin/openUserAdministration">{{_userAdministration}}</a>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasSystemRight(SystemZone.USER)) {
            append(sb, html, null);
        }
    }

}
