package de.elbe5.administration.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.MessageHtml;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class ContentSystemAdminPage extends SystemAdminPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        appendHtmlStart(sb, rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(sb, rdata);
            appendCachesStart(sb, rdata);
            appendUserCache(sb, rdata);
            appendContentCache(sb, rdata);
            appendPreviewCache(sb, rdata);
            appendCachesEnd(sb);
            appendTimerList(sb, rdata);
        }
        appendHtmlEnd(sb);
    }

    void appendContentCache(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="{2}>"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_contentCache"),
                Strings.getHtml("_reload")
        ));
    }


    void appendPreviewCache(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/clearPreviewCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_previewCache"),
                Strings.getHtml("_reload")
        ));
    }

}
