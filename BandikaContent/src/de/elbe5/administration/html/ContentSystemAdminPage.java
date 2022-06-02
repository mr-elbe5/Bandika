package de.elbe5.administration.html;

import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

import java.util.Map;

public class ContentSystemAdminPage extends SystemAdminPage {

    @Override
    public void appendPageHtml(RequestData rdata) {
        appendPageHtmlStart(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(rdata);
            appendCachesStart(rdata);
            appendUserCache(rdata);
            appendTemplateCache(rdata);
            appendContentCache(rdata);
            appendPreviewCache(sb, rdata);
            appendCachesEnd();
            appendTimerList(rdata);
        }
        appendPageHtmlEnd();
    }

    void appendContentCache(RequestData rdata) {
        append(sb, """
                        <li>
                            <span>$contentCache$</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="$reload$>"></a>
                            </div>
                        </li>
                """,
                Map.ofEntries(
                        param("contentCache","_contentCache"),
                        param("reload","_reload")
                )
        );
    }


    void appendPreviewCache(StringBuilder sb, RequestData rdata) {
        append(sb, """
                        <li>
                            <span>$previewCache$</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/clearPreviewCache" title="$reload$"></a>
                            </div>
                        </li>
                """,
                Map.ofEntries(
                        param("previewCache","_previewCache"),
                        param("reload","_reload")
                )
        );
    }

}
