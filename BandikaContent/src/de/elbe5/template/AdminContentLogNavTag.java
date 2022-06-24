package de.elbe5.template;

import de.elbe5.request.RequestData;

public class AdminContentLogNavTag extends TemplateTag {

    public static final String TYPE = "adminContentLogNav";

    public AdminContentLogNavTag() {
        this.type = TYPE;
    }

    static final String html = """
            <li class="nav-item">
                <a class="nav-link" href="/ctrl/admin/openContentLog">{{_contentLog}}</a>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyContentRight()) {
            append(sb, html, null);
        }
    }

}
