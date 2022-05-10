package de.elbe5.layout;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.html.Html;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;

public class FooterTag extends TemplateTag {

    public static final String TYPE = "footer";

    public FooterTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        sb.append(Html.format("""
                <ul class="nav">
                    <li class="nav-item">
                        <a class="nav-link">&copy; {1}
                        </a>
                    </li>
                    """, Html.localized("layout.copyright")));
        for (ContentData data : ContentCache.getFooterList()) {
            if (data.hasUserReadRight(rdata)) {
                sb.append(Html.format("""
                    <li class="nav-item">
                    <a class="nav-link" href="{1}">{2}
                    </a>
                </li>""", data.getUrl(), Html.html(data.getDisplayName())));
            }
        }
        sb.append("""
                </ul>
                """);
    }

}
