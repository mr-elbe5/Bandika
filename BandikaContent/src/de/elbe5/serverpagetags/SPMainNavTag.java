package de.elbe5.serverpagetags;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SPMainNavTag extends SPTag {

    public static final String TYPE = "mainnav";

    public SPMainNavTag() {
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
                        sb.append(format("""
                                        <li class="nav-item dropdown">
                                            <a class="nav-link {1} dropdown-toggle" data-toggle="dropdown" href="{2}" role="button" aria-haspopup="true" aria-expanded="false">{3}
                                            </a>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item {4}" href="{5}">{6}
                                                </a>
                                                """,
                                activeIds.contains(contentData.getId()) ? "active" : "",
                                contentData.getUrl(),
                                contentData.getNavDisplay(),
                                activeIds.contains(contentData.getId()) ? "active" : "",
                                contentData.getUrl(),
                                contentData.getNavDisplay()
                        ));
                        for (ContentData child : children) {
                            sb.append(format("""
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
                                """
                        );
                    } else {
                        sb.append(format("""
                                                <li class="nav-item {1}">
                                                    <a class="nav-link {2}" href="{3}">{4}
                                                    </a>
                                                </li>
                                        """,
                                activeIds.contains(contentData.getId()) ? "active" : "",
                                activeIds.contains(contentData.getId()) ? "active" : "",
                                contentData.getUrl(),
                                contentData.getNavDisplay()
                        ));
                    }
                }
            }
        }
    }

}
