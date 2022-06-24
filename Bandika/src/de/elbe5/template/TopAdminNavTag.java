package de.elbe5.template;

import de.elbe5.request.RequestData;

public class TopAdminNavTag extends TemplateTag {

    public static final String TYPE = "topAdminNav";

    public TopAdminNavTag() {
        this.type = TYPE;
    }

    static final String html = """
            <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openAdministration" title="{{_administration}}"></a></li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasAnyAdministrationRight()) {
            append(sb, html, null);
        }
    }

}
