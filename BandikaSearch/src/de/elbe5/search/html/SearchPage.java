package de.elbe5.search.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;
import de.elbe5.search.ContentSearchData;
import de.elbe5.search.ContentSearchResultData;

import java.util.Map;

public class SearchPage extends HtmlIncludePage implements IHtmlBuilder {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentSearchResultData pageResult = rdata.getAttributes().get("searchResultData", ContentSearchResultData.class);
        append(sb,"""
                        <form:message/>
                        <section class="contentTop">
                            <h1>$search$
                            </h1>
                            <form action="/page/search/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
                                <div class="input-group">
                                    <label for="searchPattern"></label><input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60" value="$pattern$"/>
                                    <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">$search$
                                    </button>
                                </div>
                            </form>
                        </section>
                        <section class="searchSection">
                            <div class="searchResults">
                            """,
                Map.ofEntries(
                        param("search","_search"),
                        param("pattern",pageResult.getPattern())
                )
        );
        if (!pageResult.getResults().isEmpty()) {
            append(sb,"""
                            <h2>$results$</h2>
                            """,
                    Map.ofEntries(
                            param("results","_searchResults")
                    )
            );
            for (ContentSearchData data : pageResult.getResults()) {
                String description = data.getDescriptionContext();
                String content = data.getContentContext();
                append(sb,"""
                                <div class="searchResult">
                                    <div class="searchTitle">
                                        <a href="$url$" title="$show$">$name$
                                        </a>
                                    </div>
                                    <div class="searchDescription">$description$
                                    </div>
                                    <div class="searchContent">$content$
                                    </div>
                                </div>
                                """,
                        Map.ofEntries(
                                param("url",data.getUrl()),
                                param("show","_show"),
                                param("name",data.getNameContext()),
                                param("description",description.isEmpty() ? "" : data.getDescriptionContext()),
                                param("content",content.isEmpty() ? "" : data.getContentContext())
                        )
                );
            }
        } else {
            append(sb,"""
                            <span>$noResults$</span>
                            """,
                    Map.ofEntries(
                            param("noResults","_noResults")
                    )
            );
        }
        sb.append("""
                    </div>
                </section>
                """);
    }
}
