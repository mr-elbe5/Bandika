package de.elbe5.administration.response;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentDayLog;
import de.elbe5.content.ContentLog;
import de.elbe5.request.RequestData;

import java.util.List;

public class ContentLogAdminPage implements IAdminPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        List<ContentDayLog> dayLogs = ContentBean.getInstance().getAllViewCounts();
        sb.append(Strings.format("""
                        <div id="pageContent">
                            <form:message/>
                            <section class="logSection">
                                <h3>{1}</h3>
                                <a class="icon fa fa-trash-o" href="/ctrl/admin/resetContentLog" title="{2}"></a>
                                """,
                Strings.getHtml("_clicksPerDay"),
                Strings.getHtml("_reset")
        ));
        if (rdata.hasAnyContentRight()) {
            sb.append("""
                    <table>
                    """);
            for (ContentDayLog dayLog : dayLogs) {
                sb.append(Strings.format("""
                                <tr>
                                    <th colspan="2">
                                        {1}
                                    </th>
                                </tr>
                                """,
                        DateHelper.toHtmlDate(dayLog.getDay())
                ));
                for (ContentLog log : dayLog.getLogs()) {
                    sb.append(Strings.format("""
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
                    ));
                }
            }
            sb.append("""
                    </table>
                    """);
        }
        sb.append("""
                    </section>
                </div>
                """);
    }


}
