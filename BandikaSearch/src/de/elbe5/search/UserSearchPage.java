package de.elbe5.search;

import de.elbe5.base.Strings;
import de.elbe5.layout.HtmlIncludePage;
import de.elbe5.request.RequestData;

public class UserSearchPage extends HtmlIncludePage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserSearchResultData userResult = rdata.getAttributes().get("searchResultData", UserSearchResultData.class);
        sb.append(Strings.format("""
                        <section class="mainSection searchResults">
                            <h1>{1}
                            </h1>
                            <table class="padded searchResultsTable">
                                <tr>
                                    <th class="col2">{2}
                                    </th>
                                    <th class="col3">{3}
                                    </th>
                                </tr>
                                """,
                Strings.getHtml("_searchResults"),
                Strings.getHtml("_name"),
                Strings.getHtml("_email")
        ));
        if (userResult != null && !userResult.getResults().isEmpty()) {
            for (UserSearchData data : userResult.getResults()) {
                sb.append(Strings.format("""
                                <tr>
                                    <td>{1}
                                    </td>
                                    <td>{2}
                                    </td>
                                </tr>
                                """,
                        data.getNameContext(),
                        data.getEmailContext()
                ));
            }
        }
        sb.append("""
                    </table>
                </section>
                """);
    }
}
