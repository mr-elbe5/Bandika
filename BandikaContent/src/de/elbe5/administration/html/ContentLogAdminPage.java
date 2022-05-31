package de.elbe5.administration.html;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentDayLog;
import de.elbe5.content.ContentLog;
import de.elbe5.request.RequestData;

import java.util.List;

public class ContentLogAdminPage extends AdminPage {

    public ContentLogAdminPage() {
        super(Strings.getString("_contentLog"));
    }

    @Override
    public void appendPageHtml(RequestData rdata) {
        List<ContentDayLog> dayLogs = ContentBean.getInstance().getAllViewCounts();
        append("""
                        <div id="pageContent">
                            <form:message/>
                            <section class="logSection">
                                <h3>{1}</h3>
                                <a class="icon fa fa-trash-o" href="/ctrl/admin/resetContentLog" title="{2}"></a>
                                """,
                Strings.getHtml("_clicksPerDay"),
                Strings.getHtml("_reset")
        );
        if (rdata.hasAnyContentRight()) {
            append("""
                    <table>
                    """);
            for (ContentDayLog dayLog : dayLogs) {
                append("""
                                <tr>
                                    <th colspan="2">
                                        {1}
                                    </th>
                                </tr>
                                """,
                        DateHelper.toHtmlDate(dayLog.getDay())
                );
                for (ContentLog log : dayLog.getLogs()) {
                    append("""
                                    <tr>
                                        <td>
                                            {1}
                                        </td>
                                        <td>
                                            {2}
                                        </td>
                                    </tr>
                                    """,
                            Strings.toHtml(ContentCache.getContent(log.getId()).getDisplayName()),
                            Integer.toString(log.getCount())
                    );
                }
            }
            append("""
                    </table>
                    """);
        }
        append("""
                    </section>
                </div>
                """);
    }


}
