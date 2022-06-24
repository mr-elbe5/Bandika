package de.elbe5.search.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;
import de.elbe5.search.ContentSearchData;
import de.elbe5.search.ContentSearchResultData;

import java.util.Map;

public class SearchPage extends HtmlIncludePage implements IHtmlBuilder {

    static final String startHtml = """
            <form:message/>
            <section class="contentTop">
                <h1>{{_search}}
                </h1>
                <form action="/page/search/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
                    <div class="input-group">
                        <label for="searchPattern"></label><input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60" value="{{pattern}}"/>
                        <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">{{search}}
                        </button>
                    </div>
                </form>
            </section>
            <section class="searchSection">
                <div class="searchResults">
            """;
    static final String headerHtml = """
                    <h2>{{_searchResults}}</h2>
            """;
    static final String resultHtml = """
                    <div class="searchResult">
                        <div class="searchTitle">
                            <a href="{{url}}" title="{{_show}}">{{name}}
                            </a>
                        </div>
                        <div class="searchDescription">{{description}}
                        </div>
                        <div class="searchContent">{{content}}
                        </div>
                    </div>
            """;
    static final String noresultHtml = """
                    <span>{{_noResults}}</span>
            """;
    static final String endHtml = """
                </div>
            </section>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentSearchResultData pageResult = rdata.getAttributes().get("searchResultData", ContentSearchResultData.class);
        append(sb, startHtml,
                Map.ofEntries(
                        Map.entry("pattern", toHtml(pageResult.getPattern()))));
        if (!pageResult.getResults().isEmpty()) {
            append(sb, headerHtml, null);
            for (ContentSearchData data : pageResult.getResults()) {
                String description = data.getDescriptionContext();
                String content = data.getContentContext();
                append(sb, resultHtml,
                        Map.ofEntries(
                                Map.entry("url", data.getUrl()),
                                Map.entry("name", data.getNameContext()),
                                Map.entry("description", description.isEmpty() ? "" : data.getDescriptionContext()),
                                Map.entry("content", content.isEmpty() ? "" : data.getContentContext())));
            }
        } else {
            append(sb, noresultHtml, null);
        }
        sb.append(endHtml);
    }
}
