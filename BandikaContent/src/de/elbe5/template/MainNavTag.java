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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData home = ContentCache.getContentRoot();
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
                        appendDropdownMenuHtml(sb, contentData, children, activeIds);
                    } else {
                        appendSingleMenuHtml(sb, contentData, activeIds);
                    }
                }
            }
        }
    }

    public void appendSingleMenuHtml(StringBuilder sb, ContentData contentData, Set<Integer> activeIds) {
        append(sb,"""
                                        <li class="nav-item main-nav $active$">
                                            <a class="nav-link $active$" href="$url$">$name$</a>
                                        </li>
            """,
                Map.ofEntries(
                        param("active",activeIds.contains(contentData.getId()) ? "active" : ""),
                        param("url",contentData.getUrl()),
                        htmlParam("name",contentData.getNavDisplay())
                )
        );
    }

    public void appendDropdownMenuHtml(StringBuilder sb, ContentData contentData, List<ContentData> children, Set<Integer> activeIds) {
        append(sb,"""
                                        <li class="nav-item main-nav dropdown">
                                            <a class="nav-link $active$ dropdown-toggle" data-toggle="dropdown" href="$url$" role="button" aria-haspopup="true" aria-expanded="false">$name$</a>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item $active$" href="$url$">$name$</a>
            """,
                Map.ofEntries(
                        param("active",activeIds.contains(contentData.getId()) ? "active" : ""),
                        param("url",contentData.getUrl()),
                        htmlParam("name",contentData.getNavDisplay())
                )
        );
        for (ContentData child : children) {
            append(sb,"""
                                                <a class="dropdown-item $active$" href="$url$">$name$</a>
            """,
                    Map.ofEntries(
                            param("active",activeIds.contains(child.getId()) ? "active" : ""),
                            param("url",child.getUrl()),
                            htmlParam("name",child.getNavDisplay())
                    )
            );
        }
        sb.append("""
                                            </div>
                                        </li>
            """);
    }

}
