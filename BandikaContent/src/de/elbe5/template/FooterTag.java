package de.elbe5.template;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;

import java.util.Map;

public class FooterTag extends TemplateTag {

    public static final String TYPE = "footer";

    public FooterTag() {
        this.type = TYPE;
    }

    static final String startHtml = """
            <ul class="nav">
                <li class="nav-item">
                    <a class="nav-link">&copy; {{_copyright}}
                    </a>
                </li>
            """;
    static final String linkHtml = """
                <li class="nav-item">
                    <a class="nav-link" href="{{url}}">{{name}}
                    </a>
                </li>
            """;
    static final String endHtml = """
            </ul>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        append(sb, startHtml, null);
        for (ContentData data : ContentCache.getInstance().getFooterList()) {
            if (data.hasUserReadRight(rdata)) {
                append(sb, linkHtml, Map.ofEntries(
                        Map.entry("url", data.getUrl()),
                        Map.entry("name", toHtml(data.getDisplayName()))));
            }
        }
        sb.append(endHtml);
    }

}
