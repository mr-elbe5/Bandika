package de.elbe5.administration.html;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentDayLog;
import de.elbe5.content.ContentLog;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class ContentLogAdminPage extends AdminPage {

    public ContentLogAdminPage() {
        super(Strings.getString("_contentLog"));
    }

    @Override
    public void appendPageHtml(RequestData rdata) {
        List<ContentDayLog> dayLogs = ContentBean.getInstance().getAllViewCounts();
        append(sb, """
                        <div id="pageContent">
                            <form:message/>
                            <section class="logSection">
                                <h3>$clicks$</h3>
                                <a class="icon fa fa-trash-o" href="/ctrl/admin/resetContentLog" title="$reset$"></a>
                                """,
                Map.ofEntries(
                        param("clicks","_clicksPerDay"),
                        param("reset","_reset")
                )
        );
        if (rdata.hasAnyContentRight()) {
            append(sb, """
                    <table>
                    """);
            for (ContentDayLog dayLog : dayLogs) {
                append(sb, """
                                <tr>
                                    <th colspan="2">$date$</th>
                                </tr>
                                """,
                        Map.ofEntries(
                                param("date",DateHelper.toHtmlDate(dayLog.getDay()))
                        )
                );
                for (ContentLog log : dayLog.getLogs()) {
                    append(sb, """
                                    <tr>
                                        <td>$name$</td>
                                        <td>$count$</td>
                                    </tr>
                                    """,
                            Map.ofEntries(
                                    param("name",ContentCache.getContent(log.getId()).getDisplayName()),
                                    param("count",log.getCount())
                            )
                    );
                }
            }
            append(sb, """
                    </table>
                    """);
        }
        append(sb, """
                    </section>
                </div>
                """);
    }


}
