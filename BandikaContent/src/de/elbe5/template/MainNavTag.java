package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.template.TemplateTag;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        sb.append(Strings.format("""
                                        <li class="nav-item main-nav {1}">
                                            <a class="nav-link {2}" href="{3}">{4}</a>
                                        </li>
            """,
                activeIds.contains(contentData.getId()) ? "active" : "",
                activeIds.contains(contentData.getId()) ? "active" : "",
                contentData.getUrl(),
                contentData.getNavDisplay()
        ));
    }

    public void appendDropdownMenuHtml(StringBuilder sb, ContentData contentData, List<ContentData> children, Set<Integer> activeIds) {
        sb.append(Strings.format("""
                                        <li class="nav-item main-nav dropdown">
                                            <a class="nav-link {1} dropdown-toggle" data-toggle="dropdown" href="{2}" role="button" aria-haspopup="true" aria-expanded="false">{3}</a>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item {4}" href="{5}">{6}</a>
            """,
                activeIds.contains(contentData.getId()) ? "active" : "",
                contentData.getUrl(),
                contentData.getNavDisplay(),
                activeIds.contains(contentData.getId()) ? "active" : "",
                contentData.getUrl(),
                contentData.getNavDisplay()
        ));
        for (ContentData child : children) {
            sb.append(Strings.format("""
                                                <a class="dropdown-item {1}" href="{2}">{3}</a>
            """,
                    activeIds.contains(child.getId()) ? "active" : "",
                    child.getUrl(),
                    child.getNavDisplay()
            ));
        }
        sb.append("""
                                            </div>
                                        </li>
            """);
    }

}
