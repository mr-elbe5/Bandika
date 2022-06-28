package de.elbe5.administration.html;

import de.elbe5.data.LocalizedStrings;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentDayLog;
import de.elbe5.content.ContentLog;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class ContentLogAdminPage extends AdminPage {

    public ContentLogAdminPage() {
        super(LocalizedStrings.getString("_contentLog"));
    }

    static final String htmlStart = """
            <div id="pageContent">
                <form:message/>
                <section class="logSection">
                    <h3>{{_clicksPerDay}}</h3>
                    <a class="icon fa fa-trash-o" href="/ctrl/admin/resetContentLog" title="{{_reset}}"></a>
            """;
    static final String tableStart = """
                    <table>
            """;
    static final String headerHtml = """
                        <tr>
                            <th colspan="2">{{date}}</th>
                        </tr>
            """;
    static final String entryHtml = """
                        <tr>
                            <td>{{name}}</td>
                            <td>{{count}}</td>
                        </tr>
            """;
    static final String tableEnd = """
                    </table>
            """;
    static final String htmlEnd = """
                </section>
            </div>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        List<ContentDayLog> dayLogs = ContentBean.getInstance().getAllViewCounts();
        append(sb, htmlStart, null);
        if (rdata.hasAnyContentRight()) {
            append(sb, tableStart);
            for (ContentDayLog dayLog : dayLogs) {
                append(sb, headerHtml,
                        Map.ofEntries(
                                Map.entry("date", toHtmlDate(dayLog.getDay()))));
                for (ContentLog log : dayLog.getLogs()) {
                    append(sb, entryHtml,
                            Map.ofEntries(
                                    Map.entry("name", toHtml(ContentCache.getInstance().getContent(log.getId()).getDisplayName())),
                                    Map.entry("count", Integer.toString(log.getCount()))));
                }
            }
            append(sb, tableEnd);
        }
        append(sb, htmlEnd);
    }


}
