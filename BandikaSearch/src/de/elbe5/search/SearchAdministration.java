package de.elbe5.search;

import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;

import java.util.Map;

public interface SearchAdministration extends IHtmlBuilder {

    default void appendSearchAdministration(StringBuilder sb, RequestData rdata) {
        append(sb,"""
                        <li class="open">
                            <span>$search</span>
                            <div class="icons">
                                <a class="icon fa fa-globe" href="/page/search/indexAllContent" title="$content"></a>
                                <a class="icon fa fa-users" href="/page/search/indexAllUsers" title="$users"></a>
                            </div>
                        </li>
                        """,
                Map.ofEntries(
                        param("search","_search"),
                        param("content","_indexAllContent"),
                        param("users","_indexAllUsers")
                )
        );
    }
}
