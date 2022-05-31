package de.elbe5.administration.html;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;

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
        append("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="{2}>"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_contentCache"),
                Strings.getHtml("_reload")
        );
    }


    void appendPreviewCache(StringBuilder sb, RequestData rdata) {
        append("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/clearPreviewCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_previewCache"),
                Strings.getHtml("_reload")
        );
    }

}
