package de.elbe5.serverpagetags;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFooterTag extends SPTag {

    public static final String TYPE = "footer";

    public SPFooterTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format("""
                <ul class="nav">
                    <li class="nav-item">
                        <a class="nav-link">&copy; {1}
                        </a>
                    </li>
                    """, localizedString("layout.copyright")));
        for (ContentData data : ContentCache.getFooterList()) {
            if (data.hasUserReadRight(rdata)) {
                sb.append(format("""
                    <li class="nav-item">
                    <a class="nav-link" href="{1}">{2}
                    </a>
                </li>""", data.getUrl(), toHtml(data.getDisplayName())));
            }
        }
        sb.append("""
                </ul>
                """);
    }

}
