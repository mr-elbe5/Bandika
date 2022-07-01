package de.elbe5.administration.html;

import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

public class ContentSystemAdminPage extends SystemAdminPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        appendPageHtmlStart(sb, rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(sb);
            appendCachesStart(sb);
            appendUserCache(sb);
            appendTemplateCache(sb);
            appendContentCache(sb);
            appendPreviewCache(sb);
            appendCachesEnd(sb);
            appendDataHtml(sb);
            appendTimerList(sb);
        }
        appendPageHtmlEnd(sb);
    }

    static final String contentCacheHtml = """
                                <li>
                                    <span>{{_contentCache}}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="{{_reload}}>"></a>
                                    </div>
                                </li>
            """;

    static final String previewCacheHtml = """
                                <li>
                                    <span>{{_previewCache}}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-recycle" href="/ctrl/admin/clearPreviewCache" title="{{_reload}}"></a>
                                    </div>
                                </li>
            """;

    void appendContentCache(StringBuilder sb) {
        append(sb, contentCacheHtml, null);
    }


    void appendPreviewCache(StringBuilder sb) {
        append(sb, previewCacheHtml, null);
    }

}
