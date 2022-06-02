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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        append(sb, """
                        <ul class="nav">
                            <li class="nav-item">
                                <a class="nav-link">&copy; $copyright$
                                </a>
                            </li>
                            """,
                Map.ofEntries(
                        param("copyright", "_copyright")
                )
        );
        for (ContentData data : ContentCache.getFooterList()) {
            if (data.hasUserReadRight(rdata)) {
                append(sb, """
                                    <li class="nav-item">
                                    <a class="nav-link" href="$url$">$name$
                                    </a>
                                </li>""",
                        Map.ofEntries(
                                param("url", data.getUrl()),
                                param("name", data.getDisplayName())
                        )
                );
            }
        }
        sb.append("""
                </ul>
                """);
    }

}
