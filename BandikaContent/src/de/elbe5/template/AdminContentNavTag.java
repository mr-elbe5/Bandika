package de.elbe5.template;

import de.elbe5.request.RequestData;

public class AdminContentNavTag extends TemplateTag {

    public static final String TYPE = "adminContentNav";

    public AdminContentNavTag() {
        this.type = TYPE;
    }

    static final String html = """
            <li class="nav-item">
                <a class="nav-link" href="/ctrl/admin/openContentAdministration">{{_contentAdministration}}</a>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(sb, html, null);
        }
    }

}
