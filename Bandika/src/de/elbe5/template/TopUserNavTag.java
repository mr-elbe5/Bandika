package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.Map;

public class TopUserNavTag extends TemplateTag {

    public static final String TYPE = "topUserNav";

    public TopUserNavTag() {
        this.type = TYPE;
    }

    static final String startHtml = """
            <li class="nav-item">
                <a class="nav-link fa {{userClass}}" data-toggle="dropdown" title="{{_user}}"></a>
                <div class="dropdown-menu">
            """;
    static final String loggedInHtml = """
                    <a class="dropdown-item" href="/ctrl/user/openProfile">{{_profile}}</a>
                    <a class="dropdown-item" href="/ctrl/user/logout">{{_logout}}</a>
            """;
    static final String loggedOutHtml = """
                    <a class="dropdown-item" href="/ctrl/user/openLogin">{{_login}}</a>
            """;
    static final String endHtml = """
                </div>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String userClass = rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
        append(sb, startHtml, Map.ofEntries(
                Map.entry("userClass", userClass)));
        if (rdata.isLoggedIn()) {
            append(sb, loggedInHtml, null);
        } else {
            append(sb, loggedOutHtml, null);
        }
        sb.append(endHtml);
    }

}
