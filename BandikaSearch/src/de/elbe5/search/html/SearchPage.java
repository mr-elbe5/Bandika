package de.elbe5.search.html;

import de.elbe5.base.Strings;
import de.elbe5.response.HtmlIncludePage;
import de.elbe5.request.RequestData;
import de.elbe5.search.ContentSearchData;
import de.elbe5.search.ContentSearchResultData;

public class SearchPage extends HtmlIncludePage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentSearchResultData pageResult = rdata.getAttributes().get("searchResultData", ContentSearchResultData.class);
        sb.append(Strings.format("""
                        <form:message/>
                        <section class="contentTop">
                            <h1>{1}
                            </h1>
                            <form action="/page/search/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
                                <div class="input-group">
                                    <label for="searchPattern"></label><input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60" value="{2}"/>
                                    <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">{3}
                                    </button>
                                </div>
                            </form>
                        </section>
                        <section class="searchSection">
                            <div class="searchResults">
                            """,
                Strings.getHtml("_search"),
                Strings.toHtml(pageResult.getPattern()),
                Strings.getHtml("_search")
        ));
        if (!pageResult.getResults().isEmpty()) {
            sb.append(Strings.format("""
                            <h2>{1}</h2>
                            """,
                    Strings.getHtml("_searchResults")
            ));
            for (ContentSearchData data : pageResult.getResults()) {
                String description = data.getDescriptionContext();
                String content = data.getContentContext();
                sb.append(Strings.format("""
                                <div class="searchResult">
                                    <div class="searchTitle">
                                        <a href="{1}" title="{2}">{3}
                                        </a>
                                    </div>
                                    <div class="searchDescription">{4}
                                    </div>
                                    <div class="searchContent">{5}
                                    </div>
                                </div>
                                """,
                        data.getUrl(),
                        Strings.getHtml("_show"),
                        data.getNameContext(),
                        description.isEmpty() ? "" : data.getDescriptionContext(),
                        content.isEmpty() ? "" : data.getContentContext()
                ));
            }
        } else {
            sb.append(Strings.format("""
                            <span>{1}</span>
                            """,
                    Strings.getHtml("_noResults")
            ));
        }
        sb.append("""
                    </div>
                </section>
                """);
    }
}
