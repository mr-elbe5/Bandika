package de.elbe5.search.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;
import de.elbe5.search.UserSearchData;
import de.elbe5.search.UserSearchResultData;

import java.util.Map;

public class UserSearchPage extends HtmlIncludePage implements IHtmlBuilder {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserSearchResultData userResult = rdata.getAttributes().get("searchResultData", UserSearchResultData.class);
        append(sb,"""
                        <section class="mainSection searchResults">
                            <h1>$results$
                            </h1>
                            <table class="padded searchResultsTable">
                                <tr>
                                    <th class="col2">$name$
                                    </th>
                                    <th class="col3">$email$
                                    </th>
                                </tr>
                                """,
                Map.ofEntries(
                        param("results","_searchResults"),
                        param("name","_name"),
                        param("email","_email")
                )
        );
        if (userResult != null && !userResult.getResults().isEmpty()) {
            for (UserSearchData data : userResult.getResults()) {
                append(sb,"""
                                <tr>
                                    <td>$name$
                                    </td>
                                    <td>$email$
                                    </td>
                                </tr>
                                """,
                        Map.ofEntries(
                                param("name",data.getNameContext()),
                                param("email",data.getEmailContext())
                        )
                );
            }
        }
        sb.append("""
                    </table>
                </section>
                """);
    }
}
