package de.elbe5.template;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.*;

public class MainNavTag extends TemplateTag {

    public static final String TYPE = "mainnav";

    public MainNavTag() {
        this.type = TYPE;
    }

    static final String singleNav = """
            <li class="nav-item main-nav {{active}}">
                <a class="nav-link {{active}}" href="{{url}}">{{name}}</a>
            </li>
            """;
    static final String dropdownNavStart = """
            <li class="nav-item main-nav dropdown">
                <a class="nav-link {{active}} dropdown-toggle" data-toggle="dropdown" href="{{url}}" role="button" aria-haspopup="true" aria-expanded="false">{{name}}</a>
                <div class="dropdown-menu">
                    <a class="dropdown-item {{active}}" href="{{url}}">{{name}}</a>
            """;
    static final String dropdownNavLink = """
                    <a class="dropdown-item {{active}}" href="{{url}}">{{name}}</a>
            """;
    static final String dropdownNavEnd = """
                </div>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData home = ContentCache.getInstance().getContentRoot();
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        if (currentContent == null)
            currentContent = home;
        if (home != null) {
            Set<Integer> activeIds = new HashSet<>();
            currentContent.collectParentIds(activeIds);
            activeIds.add(currentContent.getId());
            for (ContentData contentData : home.getChildren()) {
                if (contentData.isInHeaderNav() && contentData.hasUserReadRight(rdata)) {
                    List<ContentData> children = new ArrayList<>();
                    for (ContentData child : contentData.getChildren()) {
                        if (child.isInHeaderNav() && child.hasUserReadRight(rdata))
                            children.add(child);
                    }
                    if (!children.isEmpty()) {
                        append(sb, dropdownNavStart, Map.ofEntries(
                                Map.entry("active", activeIds.contains(contentData.getId()) ? "active" : ""),
                                Map.entry("url", contentData.getUrl()),
                                Map.entry("name", contentData.getNavDisplayHtml())));
                        for (ContentData child : children) {
                            append(sb, dropdownNavLink, Map.ofEntries(
                                    Map.entry("active", activeIds.contains(child.getId()) ? "active" : ""),
                                    Map.entry("url", child.getUrl()),
                                    Map.entry("name", child.getNavDisplayHtml())));
                        }
                        sb.append(dropdownNavEnd);
                    } else {
                        append(sb, singleNav, Map.ofEntries(
                                Map.entry("active", activeIds.contains(contentData.getId()) ? "active" : ""),
                                Map.entry("url", contentData.getUrl()),
                                Map.entry("name", contentData.getNavDisplayHtml())));
                    }
                }
            }
        }
    }

}
