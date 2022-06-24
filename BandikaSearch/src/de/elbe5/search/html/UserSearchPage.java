package de.elbe5.search.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;
import de.elbe5.search.UserSearchData;
import de.elbe5.search.UserSearchResultData;

import java.util.Map;

public class UserSearchPage extends HtmlIncludePage implements IHtmlBuilder {

    static final String startHtml = """
            <section class="mainSection searchResults">
                <h1>{{_searchResults}}
                </h1>
                <table class="padded searchResultsTable">
                    <tr>
                        <th class="col2">{{_name}}
                        </th>
                        <th class="col3">{{_email}}
                        </th>
                    </tr>
            """;
    static final String lineHtml = """
                    <tr>
                        <td>{{name}}
                        </td>
                        <td>{{email}}
                        </td>
                    </tr>
            """;
    static final String endHtml = """
                </table>
            </section>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserSearchResultData userResult = rdata.getAttributes().get("searchResultData", UserSearchResultData.class);
        append(sb, startHtml, null);
        if (userResult != null && !userResult.getResults().isEmpty()) {
            for (UserSearchData data : userResult.getResults()) {
                append(sb, lineHtml,
                        Map.ofEntries(
                                Map.entry("name", data.getNameContext()),
                                Map.entry("email", data.getEmailContext())));
            }
        }
        sb.append(endHtml);
    }
}
