package de.elbe5.search;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;

public class SearchAdministration {

    static void appendSearchAdministration(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-globe" href="/page/search/indexAllContent" title="{2}"></a>
                                <a class="icon fa fa-users" href="/page/search/indexAllUsers" title="{3}"></a>
                            </div>
                        </li>
                        """,
                Strings.getHtml("_search"),
                Strings.getHtml("_indexAllContent"),
                Strings.getHtml("_indexAllUsers")
        ));
    }
}
