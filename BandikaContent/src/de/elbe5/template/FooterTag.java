package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.template.TemplateTag;
import de.elbe5.request.RequestData;

public class FooterTag extends TemplateTag {

    public static final String TYPE = "footer";

    public FooterTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                <ul class="nav">
                    <li class="nav-item">
                        <a class="nav-link">&copy; {1}
                        </a>
                    </li>
                    """, Strings.getHtml("_copyright")));
        for (ContentData data : ContentCache.getFooterList()) {
            if (data.hasUserReadRight(rdata)) {
                sb.append(Strings.format("""
                    <li class="nav-item">
                    <a class="nav-link" href="{1}">{2}
                    </a>
                </li>""", data.getUrl(), Strings.toHtml(data.getDisplayName())));
            }
        }
        sb.append("""
                </ul>
                """);
    }

}
