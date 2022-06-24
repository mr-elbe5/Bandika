package de.elbe5.search;

import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;

import java.util.Map;

public interface SearchAdministration extends IHtmlBuilder {

    static final String html = """
            <li class="open">
                <span>{{_search}}</span>
                <div class="icons">
                    <a class="icon fa fa-globe" href="/page/search/indexAllContent" title="{{_indexAllContent}}"></a>
                    <a class="icon fa fa-users" href="/page/search/indexAllUsers" title="{{_indexAllUsers}}"></a>
                </div>
            </li>
            """;

    default void appendSearchAdministration(StringBuilder sb, RequestData rdata) {
        append(sb, html, null);
    }
}
